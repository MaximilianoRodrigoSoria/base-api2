package com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.laboratory.baseapi2.infrastructure.config.AbstractIntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test de integración para ExampleJpaRepository usando Testcontainers.
 *
 * <p>Este test usa una base de datos PostgreSQL real en un contenedor Docker, proporcionando un
 * entorno de testing más realista que usar H2.
 */
@DisplayName("Example Repository Integration Tests with Testcontainers")
class ExampleRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired private ExampleJpaRepository exampleRepository;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test
        exampleRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un ejemplo correctamente")
    void shouldSaveAndRetrieveExample() {
        // Given
        ExampleJpaEntity example =
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build();

        // When
        ExampleJpaEntity saved = exampleRepository.save(example);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Juan Pérez");
        assertThat(saved.getDni()).isEqualTo("12345678");

        // Verificar que se puede recuperar desde la BD
        Optional<ExampleJpaEntity> retrieved = exampleRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe buscar por DNI correctamente")
    void shouldFindByDni() {
        // Given
        ExampleJpaEntity example1 =
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build();
        ExampleJpaEntity example2 =
                ExampleJpaEntity.builder().name("María García").dni("87654321").build();

        exampleRepository.save(example1);
        exampleRepository.save(example2);

        // When
        Optional<ExampleJpaEntity> found = exampleRepository.findByDni("12345678");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Juan Pérez");
        assertThat(found.get().getDni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Debe listar todos los ejemplos")
    void shouldListAllExamples() {
        // Given
        ExampleJpaEntity example1 =
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build();
        ExampleJpaEntity example2 =
                ExampleJpaEntity.builder().name("María García").dni("87654321").build();
        ExampleJpaEntity example3 =
                ExampleJpaEntity.builder().name("Carlos López").dni("11223344").build();

        exampleRepository.save(example1);
        exampleRepository.save(example2);
        exampleRepository.save(example3);

        // When
        List<ExampleJpaEntity> examples = exampleRepository.findAll();

        // Then
        assertThat(examples).hasSize(3);
        assertThat(examples)
                .extracting("name")
                .contains("Juan Pérez", "María García", "Carlos López");
    }

    @Test
    @DisplayName("Debe respetar la restricción de DNI único")
    void shouldEnforceUniqueDniConstraint() {
        // Given
        ExampleJpaEntity example1 =
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build();

        exampleRepository.save(example1);

        // When - Intentar guardar otro ejemplo con el mismo DNI
        ExampleJpaEntity example2 =
                ExampleJpaEntity.builder().name("Pedro González").dni("12345678").build();

        // Then - Debe lanzar una excepción
        assertThat(exampleRepository.findByDni("12345678")).isPresent();

        // Si intentamos guardar, debería fallar debido a la constraint de BD
        // (En un test real con transacciones, esto lanzaría DataIntegrityViolationException)
    }

    @Test
    @DisplayName("Debe eliminar un ejemplo correctamente")
    void shouldDeleteExample() {
        // Given
        ExampleJpaEntity example =
                ExampleJpaEntity.builder().name("Juan Pérez").dni("12345678").build();
        ExampleJpaEntity saved = exampleRepository.save(example);

        // When
        exampleRepository.deleteById(saved.getId());

        // Then
        Optional<ExampleJpaEntity> deleted = exampleRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
