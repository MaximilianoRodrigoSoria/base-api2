package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence.ExampleJpaEntity;
import com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence.ExampleJpaRepository;
import com.ar.laboratory.baseapi2.infrastructure.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Test de integración completo (end-to-end) para ExampleController usando Testcontainers.
 *
 * <p>Este test valida el flujo completo desde el controlador hasta la base de datos usando
 * PostgreSQL real en contenedor. Migrado de TestRestTemplate a WebTestClient para mejor soporte de
 * APIs modernas y mejor API fluida.
 */
@DisplayName("Example Controller Integration Tests with Testcontainers")
class ExampleControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private WebTestClient webTestClient;

    @Autowired private ExampleJpaRepository exampleRepository;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        exampleRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/examples - Debe crear un ejemplo correctamente")
    void shouldCreateExample() {
        // Given
        CreateExampleRequest request =
                CreateExampleRequest.builder().name("Juan Pérez").dni("12345678").build();

        // When & Then
        webTestClient
                .post()
                .uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ExampleResponse.class)
                .value(
                        response -> {
                            assertThat(response).isNotNull();
                            assertThat(response.getId()).isNotNull();
                            assertThat(response.getName()).isEqualTo("Juan Pérez");
                            assertThat(response.getDni()).isEqualTo("12345678");
                        });

        // Verificar que se guardó en la base de datos
        assertThat(exampleRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/v1/examples - Debe listar todos los ejemplos")
    void shouldListAllExamples() {
        // Given
        exampleRepository.save(
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build());
        exampleRepository.save(
                ExampleJpaEntity.builder().name("María García").dni("87654321").build());

        // When & Then
        webTestClient
                .get()
                .uri("/api/v1/examples")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ExampleResponse.class)
                .value(
                        examples -> {
                            assertThat(examples).isNotNull();
                            assertThat(examples).hasSize(2);
                        });
    }

    @Test
    @DisplayName("GET /api/v1/examples/dni/{dni} - Debe encontrar un ejemplo por DNI")
    void shouldFindExampleByDni() {
        // Given
        exampleRepository.save(
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build());

        // When & Then
        webTestClient
                .get()
                .uri("/api/v1/examples/dni/12345678")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ExampleResponse.class)
                .value(
                        response -> {
                            assertThat(response).isNotNull();
                            assertThat(response.getName()).isEqualTo("Juan Pérez");
                            assertThat(response.getDni()).isEqualTo("12345678");
                        });
    }

    @Test
    @DisplayName("GET /api/v1/examples/dni/{dni} - Debe retornar 404 si no existe")
    void shouldReturn404WhenExampleNotFound() {
        // When & Then
        webTestClient
                .get()
                .uri("/api/v1/examples/dni/99999999")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("POST /api/v1/examples - Debe retornar 400 con DNI duplicado")
    void shouldReturn400WhenDniAlreadyExists() {
        // Given - Crear primero un ejemplo
        exampleRepository.save(
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build());

        CreateExampleRequest request =
                CreateExampleRequest.builder().name("Pedro González").dni("12345678").build();

        // When & Then
        webTestClient
                .post()
                .uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
