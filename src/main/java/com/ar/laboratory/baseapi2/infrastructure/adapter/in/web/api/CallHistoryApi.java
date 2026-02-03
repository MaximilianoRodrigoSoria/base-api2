package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web.api;

import com.ar.laboratory.baseapi2.application.dto.CallHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrato API para consultar el historial de llamadas a endpoints. Esta interfaz define todos los
 * endpoints relacionados con Call History y contiene la documentación OpenAPI completa.
 */
@Tag(name = "Call History", description = "API para consultar el historial de llamadas a endpoints")
public interface CallHistoryApi {

    @Operation(
            summary = "Listar historial de llamadas",
            description =
                    "Obtiene una lista paginada del historial de llamadas ordenadas por fecha"
                            + " descendente")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> listAll(
            @Parameter(description = "Cantidad de registros a retornar", example = "50")
                    @RequestParam(defaultValue = "50")
                    int limit,
            @Parameter(description = "Offset para paginación", example = "0")
                    @RequestParam(defaultValue = "0")
                    int offset);

    @Operation(
            summary = "Buscar por ID",
            description = "Obtiene un registro específico del historial por su ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Registro encontrado",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class))),
                @ApiResponse(responseCode = "404", description = "Registro no encontrado")
            })
    @StandardApiResponses
    ResponseEntity<CallHistoryResponse> findById(
            @Parameter(description = "ID del registro", example = "1") @PathVariable Long id);

    @Operation(
            summary = "Buscar por rango de fechas",
            description = "Obtiene registros del historial dentro de un rango de fechas específico")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> findByDateRange(
            @Parameter(description = "Fecha desde (ISO format)", example = "2026-02-03T00:00:00")
                    @RequestParam
                    LocalDateTime from,
            @Parameter(description = "Fecha hasta (ISO format)", example = "2026-02-03T23:59:59")
                    @RequestParam
                    LocalDateTime to);

    @Operation(
            summary = "Buscar por Correlation ID",
            description =
                    "Obtiene todos los registros asociados a un Correlation ID específico. Útil"
                            + " para rastrear transacciones distribuidas")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> findByCorrelationId(
            @Parameter(
                            description = "Correlation ID para rastrear llamadas relacionadas",
                            example = "abc-123-xyz")
                    @PathVariable
                    String correlationId);

    @Operation(
            summary = "Buscar por path",
            description = "Obtiene todos los registros para un endpoint específico")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> findByPath(
            @Parameter(description = "Path del endpoint", example = "/api/v1/examples")
                    @RequestParam
                    String path);

    @Operation(
            summary = "Buscar llamadas exitosas o fallidas",
            description =
                    "Filtra el historial por el estado de éxito de las llamadas. Útil para"
                            + " debugging y análisis de errores")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> findBySuccess(
            @Parameter(
                            description =
                                    "Estado de éxito: true para exitosas, false para fallidas",
                            example = "false")
                    @RequestParam
                    boolean success);

    @Operation(
            summary = "Buscar llamadas fallidas (atajo)",
            description = "Obtiene todas las llamadas que fallaron. Equivalente a success=false")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                CallHistoryResponse.class)))
            })
    @StandardApiResponses
    ResponseEntity<List<CallHistoryResponse>> findFailures();
}
