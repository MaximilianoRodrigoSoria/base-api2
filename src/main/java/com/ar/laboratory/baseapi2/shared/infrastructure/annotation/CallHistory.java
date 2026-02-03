package com.ar.laboratory.baseapi2.shared.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para registrar el historial de llamadas a endpoints de forma automática. Cuando se
 * aplica a un método de controller, el aspect CallHistoryAspect interceptará la ejecución y
 * guardará información sobre la llamada (request, response, tiempos, errores, etc.) de forma
 * asíncrona en la base de datos.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallHistory {

    /**
     * Acción o descripción de la operación
     *
     * @return Nombre de la acción
     */
    String action() default "";

    /**
     * Si se debe registrar el body del request
     *
     * @return true para registrar el request body
     */
    boolean logRequest() default true;

    /**
     * Si se debe registrar el body de la response
     *
     * @return true para registrar el response body
     */
    boolean logResponse() default true;

    /**
     * Campos sensibles que deben ser enmascarados en los logs (passwords, tokens, etc.)
     *
     * @return Array de nombres de campos a enmascarar
     */
    String[] maskFields() default {"password", "token", "authorization", "secret"};

    /**
     * Tamaño máximo del payload (request/response) a guardar en caracteres
     *
     * @return Máximo de caracteres
     */
    int maxPayloadSize() default 4096;
}
