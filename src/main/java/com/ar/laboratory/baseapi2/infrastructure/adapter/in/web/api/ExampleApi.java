package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web.api;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Contrato API para la gestión de ejemplos. Esta interfaz define todos los endpoints relacionados
 * con Examples y contiene la documentación OpenAPI completa.
 */
@Tag(name = "Examples", description = "API para gestión de ejemplos")
public interface ExampleApi {

    @Operation(
            summary = "Crear un nuevo ejemplo",
            description = "Crea un nuevo ejemplo con nombre y DNI únicos")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Ejemplo creado exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request);

    @Operation(
            summary = "Listar todos los ejemplos",
            description = "Obtiene la lista completa de todos los ejemplos registrados")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<ExampleResponse>> listAll();

    @Operation(
            summary = "Buscar ejemplo por DNI",
            description =
                    "Busca un ejemplo específico por su DNI. Los resultados se cachean en Redis"
                            + " para mejorar el rendimiento")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ejemplo encontrado exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class))),
                @ApiResponse(responseCode = "404", description = "Ejemplo no encontrado")
            })
    @StandardApiResponses
    ResponseEntity<ExampleResponse> findByDni(
            @Parameter(
                            description = "DNI del ejemplo a buscar",
                            required = true,
                            example = "12345678")
                    @PathVariable
                    String dni);
}
