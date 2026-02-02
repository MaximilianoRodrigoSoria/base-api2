package com.ar.laboratory.baseapi2.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.domain.exception.ExampleAlreadyExistsException;
import com.ar.laboratory.baseapi2.domain.model.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateExampleService Tests")
class CreateExampleServiceTest {

    @Mock private ExampleRepositoryPort exampleRepositoryPort;

    @InjectMocks private CreateExampleService createExampleService;

    private CreateExampleRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = CreateExampleRequest.builder().name("John Doe").dni("12345678").build();
    }

    @Test
    @DisplayName("Debe crear un Example exitosamente")
    void shouldCreateExampleSuccessfully() {
        // Given
        Example savedExample =
                Example.builder()
                        .id(1L)
                        .name(validRequest.getName())
                        .dni(validRequest.getDni())
                        .build();

        when(exampleRepositoryPort.existsByDni(anyString())).thenReturn(false);
        when(exampleRepositoryPort.save(any(Example.class))).thenReturn(savedExample);

        // When
        ExampleResponse response = createExampleService.create(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo(validRequest.getName());
        assertThat(response.getDni()).isEqualTo(validRequest.getDni());

        verify(exampleRepositoryPort, times(1)).existsByDni(validRequest.getDni());
        verify(exampleRepositoryPort, times(1)).save(any(Example.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el DNI ya existe")
    void shouldThrowExceptionWhenDniAlreadyExists() {
        // Given
        when(exampleRepositoryPort.existsByDni(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createExampleService.create(validRequest))
                .isInstanceOf(ExampleAlreadyExistsException.class)
                .hasMessageContaining("Ya existe un Example con DNI");

        verify(exampleRepositoryPort, times(1)).existsByDni(validRequest.getDni());
        verify(exampleRepositoryPort, never()).save(any(Example.class));
    }
}
