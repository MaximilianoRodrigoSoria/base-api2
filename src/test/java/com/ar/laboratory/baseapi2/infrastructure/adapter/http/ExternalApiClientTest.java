package com.ar.laboratory.baseapi2.infrastructure.adapter.http;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Ejemplo de test usando WireMock para simular APIs HTTP externas.
 *
 * <p>WireMock permite crear un servidor HTTP mock para simular respuestas de servicios externos,
 * útil para tests de integración con APIs de terceros.
 *
 * <p>Este test ha sido migrado de HttpClient a WebClient para un enfoque más moderno y reactivo,
 * con mejor integración con el ecosistema Spring.
 */
@DisplayName("External API Client Tests with WireMock and WebClient")
class ExternalApiClientTest {

    private WireMockServer wireMockServer;
    private WebClient webClient;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        // Configurar WireMock en un puerto aleatorio
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        // Configurar el cliente para apuntar al servidor mock
        baseUrl = "http://localhost:" + wireMockServer.port();
        WireMock.configureFor("localhost", wireMockServer.port());

        // Configurar WebClient para las peticiones
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @AfterEach
    void tearDown() {
        // Detener el servidor WireMock después de cada test
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    @DisplayName("Debe simular una respuesta exitosa de API externa")
    void shouldMockSuccessfulApiResponse() {
        // Given - Configurar el mock para responder a una petición específica
        stubFor(
                get(urlEqualTo("/api/users/123"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {
                                    "id": 123,
                                    "name": "Juan Pérez",
                                    "email": "juan@example.com"
                                }
                                """)));

        // When - Hacer la petición al servidor mock usando WebClient
        String responseBody =
                webClient.get().uri("/api/users/123").retrieve().bodyToMono(String.class).block();

        // Then - Verificar la respuesta
        assertThat(responseBody).isNotNull();
        assertThat(responseBody).contains("Juan Pérez");
        assertThat(responseBody).contains("juan@example.com");

        // Verificar que la petición fue recibida
        verify(getRequestedFor(urlEqualTo("/api/users/123")));
    }

    @Test
    @DisplayName("Debe simular un error 404 de API externa")
    void shouldMockNotFoundResponse() {
        // Given
        stubFor(
                get(urlEqualTo("/api/users/999"))
                        .willReturn(
                                aResponse()
                                        .withStatus(404)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {
                                    "error": "User not found",
                                    "code": "USER_NOT_FOUND"
                                }
                                """)));

        // When - Usar exchangeToMono para capturar el status code
        ClientResponse response =
                webClient.get().uri("/api/users/999").exchangeToMono(Mono::just).block();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String body = response.bodyToMono(String.class).block();
        assertThat(body).contains("User not found");
    }

    @Test
    @DisplayName("Debe simular una petición POST con cuerpo")
    void shouldMockPostRequest() {
        // Given
        stubFor(
                post(urlEqualTo("/api/users"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(matchingJsonPath("$.name", equalTo("María García")))
                        .willReturn(
                                aResponse()
                                        .withStatus(201)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {
                                    "id": 456,
                                    "name": "María García",
                                    "email": "maria@example.com"
                                }
                                """)));

        // When
        String requestBody =
                """
                {
                    "name": "María García",
                    "email": "maria@example.com"
                }
                """;

        ClientResponse response =
                webClient
                        .post()
                        .uri("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .exchangeToMono(Mono::just)
                        .block();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);

        String body = response.bodyToMono(String.class).block();
        assertThat(body).contains("María García");

        // Verificar que se hizo exactamente una petición
        verify(1, postRequestedFor(urlEqualTo("/api/users")));
    }

    @Test
    @DisplayName("Debe simular timeout o retraso en la respuesta")
    void shouldMockDelayedResponse() {
        // Given - Configurar un retraso de 100ms
        stubFor(
                get(urlEqualTo("/api/slow-endpoint"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withFixedDelay(100) // Retraso de 100ms
                                        .withBody("Slow response")));

        // When
        long startTime = System.currentTimeMillis();
        String responseBody =
                webClient
                        .get()
                        .uri("/api/slow-endpoint")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(responseBody).isEqualTo("Slow response");
        assertThat(endTime - startTime).isGreaterThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Debe verificar headers en las peticiones")
    void shouldVerifyRequestHeaders() {
        // Given
        stubFor(
                get(urlEqualTo("/api/protected"))
                        .withHeader("Authorization", equalTo("Bearer token123"))
                        .willReturn(aResponse().withStatus(200).withBody("Protected resource")));

        // When
        String responseBody =
                webClient
                        .get()
                        .uri("/api/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token123")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        // Then
        assertThat(responseBody).isEqualTo("Protected resource");
        verify(
                getRequestedFor(urlEqualTo("/api/protected"))
                        .withHeader("Authorization", equalTo("Bearer token123")));
    }

    @Test
    @DisplayName("Debe manejar múltiples headers personalizados")
    void shouldHandleMultipleCustomHeaders() {
        // Given
        stubFor(
                get(urlEqualTo("/api/data"))
                        .withHeader("X-API-Key", equalTo("secret123"))
                        .withHeader("X-Request-Id", matching(".*"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{\"data\": \"custom response\"}")));

        // When
        String responseBody =
                webClient
                        .get()
                        .uri("/api/data")
                        .header("X-API-Key", "secret123")
                        .header("X-Request-Id", "req-12345")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        // Then
        assertThat(responseBody).contains("custom response");
        verify(
                getRequestedFor(urlEqualTo("/api/data"))
                        .withHeader("X-API-Key", equalTo("secret123"))
                        .withHeader("X-Request-Id", matching("req-.*")));
    }
}
