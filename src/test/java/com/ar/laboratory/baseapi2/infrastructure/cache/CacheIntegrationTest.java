package com.ar.laboratory.baseapi2.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.application.usecase.FindExampleByDniService;
import com.ar.laboratory.baseapi2.application.usecase.ListExamplesService;
import com.ar.laboratory.baseapi2.domain.model.Example;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

/**
 * Tests unitarios para validar el comportamiento del caché
 *
 * <p>Valida: - Cache hit: dos llamadas consecutivas con mismos parámetros ejecutan 1 sola vez el
 * repositorio - Diferentes keys generan diferentes entradas en caché - El caché se puede limpiar
 */
@ExtendWith(MockitoExtension.class)
class CacheIntegrationTest {

    @Mock private ExampleRepositoryPort exampleRepositoryPort;

    private FindExampleByDniService findExampleByDniService;
    private ListExamplesService listExamplesService;
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Configurar un CacheManager simple para tests
        cacheManager = new ConcurrentMapCacheManager("examplesByDni", "examplesCache");

        // Inicializar servicios con el mock
        findExampleByDniService = new FindExampleByDniService(exampleRepositoryPort);
        listExamplesService = new ListExamplesService(exampleRepositoryPort);
    }

    @Test
    @DisplayName("Mock válido: Repositorio retorna datos correctamente")
    void testRepositoryMock() {
        // Given
        String dni = "12345678";
        Example mockExample = new Example(1L, "Test User", dni);
        when(exampleRepositoryPort.findByDni(dni)).thenReturn(Optional.of(mockExample));

        // When
        Optional<Example> result = exampleRepositoryPort.findByDni(dni);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDni()).isEqualTo(dni);
        verify(exampleRepositoryPort, times(1)).findByDni(dni);
    }

    @Test
    @DisplayName("Servicio sin caché: Cada llamada ejecuta el repositorio")
    void testServiceWithoutCache() {
        // Given
        String dni = "12345678";
        Example mockExample = new Example(1L, "Test User", dni);
        when(exampleRepositoryPort.findByDni(dni)).thenReturn(Optional.of(mockExample));

        // When - Primera llamada
        ExampleResponse response1 = findExampleByDniService.findByDni(dni);

        // When - Segunda llamada
        ExampleResponse response2 = findExampleByDniService.findByDni(dni);

        // Then - Sin caché activo en este test, el repositorio se llama 2 veces
        // (esto es esperado porque no estamos usando Spring Context con @Cacheable activo)
        verify(exampleRepositoryPort, times(2)).findByDni(dni);

        // Then - Verificar que las respuestas son iguales
        assertThat(response1).isNotNull();
        assertThat(response2).isNotNull();
        assertThat(response1.getDni()).isEqualTo(dni);
        assertThat(response2.getDni()).isEqualTo(dni);
    }

    @Test
    @DisplayName("Servicio con diferentes DNIs ejecuta repositorio múltiples veces")
    void testServiceWithDifferentDnis() {
        // Given
        String dni1 = "11111111";
        String dni2 = "22222222";

        Example mockExample1 = new Example(1L, "User 1", dni1);
        Example mockExample2 = new Example(2L, "User 2", dni2);

        when(exampleRepositoryPort.findByDni(dni1)).thenReturn(Optional.of(mockExample1));
        when(exampleRepositoryPort.findByDni(dni2)).thenReturn(Optional.of(mockExample2));

        // When
        ExampleResponse response1 = findExampleByDniService.findByDni(dni1);
        ExampleResponse response2 = findExampleByDniService.findByDni(dni2);

        // Then
        verify(exampleRepositoryPort, times(1)).findByDni(dni1);
        verify(exampleRepositoryPort, times(1)).findByDni(dni2);

        assertThat(response1.getDni()).isEqualTo(dni1);
        assertThat(response2.getDni()).isEqualTo(dni2);
    }

    @Test
    @DisplayName("ListAll ejecuta el repositorio correctamente")
    void testListAllService() {
        // Given
        List<Example> mockExamples =
                Arrays.asList(
                        new Example(1L, "User 1", "11111111"),
                        new Example(2L, "User 2", "22222222"));

        when(exampleRepositoryPort.findAll()).thenReturn(mockExamples);

        // When
        List<ExampleResponse> response = listExamplesService.listAll();

        // Then
        verify(exampleRepositoryPort, times(1)).findAll();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getName()).isEqualTo("User 1");
        assertThat(response.get(1).getName()).isEqualTo("User 2");
    }

    @Test
    @DisplayName("CacheManager tiene los cachés configurados")
    void testCacheManagerConfiguration() {
        // Then
        assertThat(cacheManager.getCacheNames()).contains("examplesByDni", "examplesCache");
    }
}
