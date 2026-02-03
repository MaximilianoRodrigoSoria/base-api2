package com.ar.laboratory.baseapi2.infrastructure.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base para tests de integración con Testcontainers.
 *
 * <p>Esta clase configura un contenedor PostgreSQL real para los tests, proporcionando un entorno
 * de testing más cercano a producción que usar H2. Los contenedores se inician automáticamente
 * antes de los tests y se detienen después.
 *
 * <p>Uso: Extiende esta clase en tus tests de integración que necesiten base de datos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers")
public abstract class AbstractIntegrationTest {

    /**
     * Contenedor PostgreSQL compartido entre todos los tests.
     *
     * <p>El contenedor se inicia una vez y se reutiliza para todos los tests, mejorando el
     * rendimiento. La anotación @ServiceConnection configura automáticamente Spring Boot para usar
     * este contenedor.
     */
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    // Los contenedores se pueden compartir entre tests para mejorar performance
    // usando el método estático que garantiza una única instancia
}
