package com.ar.laboratory.baseapi2.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos/endpoints que deben registrar historial de llamadas. Cuando se
 * aplica a un método, el sistema capturará automáticamente: - Fecha/hora de la llamada - Identidad
 * del endpoint/método - Parámetros (request) - Resultado (response) o error - Código HTTP - Tiempo
 * de ejecución - Metadatos: correlationId, IP, usuario, etc.
 *
 * <p>Ejemplo de uso:
 *
 * <pre>
 * &#64;CallHistory(action = "CREATE_EXAMPLE", logRequest = true, logResponse = true)
 * public ExampleResponse createExample(CreateExampleRequest request) {
 *     // ...
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CallHistory {

    /**
     * Nombre lógico de la acción que se está ejecutando. Por defecto, se usará el nombre del
     * método.
     */
    String action() default "";

    /** Indica si se debe registrar el request/parámetros de entrada. Por defecto: true */
    boolean logRequest() default true;

    /**
     * Indica si se debe registrar la respuesta. Por defecto: false (para evitar payloads grandes)
     */
    boolean logResponse() default false;

    /**
     * Lista de nombres de campos sensibles que deben ser enmascarados en el logging. Útil para
     * ocultar passwords, tokens, etc.
     */
    String[] maskFields() default {"password", "token", "cvv", "pin", "secret"};

    /** Tamaño máximo en caracteres para el payload (request/response). Por defecto: 10KB */
    int maxPayloadSize() default 10240;
}
