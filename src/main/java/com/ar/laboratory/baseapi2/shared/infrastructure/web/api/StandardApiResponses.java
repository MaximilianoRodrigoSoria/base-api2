package com.ar.laboratory.baseapi2.shared.infrastructure.web.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Interfaz que define respuestas estándar de API para documentación OpenAPI
 *
 * <p>Las interfaces que extiendan esta heredarán las respuestas comunes (401, 500, etc.)
 */
@ApiResponses(
        value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
        })
public interface StandardApiResponses {

    /** Clase interna para documentar la estructura de respuestas de error */
    class ErrorResponse {
        public String timestamp;
        public int status;
        public String error;
        public String message;
        public String path;
        public String traceId;
    }
}
