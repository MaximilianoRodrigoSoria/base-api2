package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-anotación que agrupa las respuestas HTTP estándar de error comunes en todos los endpoints.
 * Reduce duplicación de código en la documentación OpenAPI.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
        value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida o error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
public @interface StandardApiResponses {}
