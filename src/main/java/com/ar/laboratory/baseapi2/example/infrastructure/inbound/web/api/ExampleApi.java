package com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.api;

import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.shared.infrastructure.web.api.StandardApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Contrato OpenAPI para el API de Examples
 *
 * <p>Define la documentación Swagger para todos los endpoints de Example
 */
@Tag(name = "Examples", description = "API para gestión de Examples")
public interface ExampleApi extends StandardApiResponses {

    @Operation(
            summary = "Crear un nuevo Example",
            description = "Crea un nuevo Example con los datos proporcionados")
    @ApiResponse(
            responseCode = "201",
            description = "Example creado exitosamente",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Ya existe un Example con ese DNI")
    ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request);

    @Operation(
            summary = "Listar todos los Examples",
            description = "Obtiene una lista de todos los Examples registrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de Examples",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleResponse.class)))
    ResponseEntity<List<ExampleResponse>> listAll();

    @Operation(
            summary = "Buscar Example por DNI",
            description = "Busca un Example específico por su DNI")
    @ApiResponse(
            responseCode = "200",
            description = "Example encontrado",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleResponse.class)))
    @ApiResponse(responseCode = "404", description = "Example no encontrado")
    ResponseEntity<ExampleResponse> findByDni(
            @Parameter(description = "DNI del Example a buscar", required = true) @PathVariable
                    String dni);
}
