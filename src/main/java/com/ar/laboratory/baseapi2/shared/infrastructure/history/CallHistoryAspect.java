package com.ar.laboratory.baseapi2.shared.infrastructure.history;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.shared.infrastructure.annotation.CallHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect AOP que intercepta métodos anotados con @CallHistory y registra información de la llamada
 * de forma asíncrona.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CallHistoryAspect {

    private final CallHistoryAsyncWriter asyncWriter;
    private final ObjectMapper objectMapper;

    /**
     * Intercepta métodos anotados con @CallHistory
     *
     * @param joinPoint Punto de interceptación
     * @param callHistory Anotación con configuración
     * @return Resultado del método interceptado
     * @throws Throwable Si ocurre un error en el método interceptado
     */
    @Around("@annotation(callHistory)")
    public Object logCallHistory(ProceedingJoinPoint joinPoint, CallHistory callHistory)
            throws Throwable {
        long startTime = System.currentTimeMillis();

        CallHistoryRecord.CallHistoryRecordBuilder recordBuilder =
                CallHistoryRecord.builder()
                        .createdAt(LocalDateTime.now())
                        .success(true)
                        .correlationId(getCorrelationId())
                        .traceId(getTraceId());

        // Capturar información del método
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String action =
                callHistory.action().isEmpty() ? methodName.toUpperCase() : callHistory.action();

        recordBuilder.handler(className + "#" + methodName + " [" + action + "]");

        // Capturar información HTTP si disponible
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            recordBuilder
                    .httpMethod(request.getMethod())
                    .path(request.getRequestURI())
                    .clientIp(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .queryParams(serializeQueryParams(request));
        }

        // Capturar request body si está habilitado
        if (callHistory.logRequest() && joinPoint.getArgs().length > 0) {
            String requestBody =
                    serializePayload(
                            joinPoint.getArgs(),
                            callHistory.maskFields(),
                            callHistory.maxPayloadSize());
            recordBuilder.requestBody(requestBody);
        }

        Object result = null;
        try {
            // Ejecutar el método interceptado
            result = joinPoint.proceed();

            // Capturar respuesta si está habilitado
            if (callHistory.logResponse() && result != null) {
                String responseBody =
                        serializePayload(
                                result, callHistory.maskFields(), callHistory.maxPayloadSize());
                recordBuilder.responseBody(responseBody);
            }

            // Si hay response HTTP, capturar status
            if (request != null) {
                // En controller, Spring establece el status después
                recordBuilder.httpStatus(200); // Por defecto, se puede mejorar con interceptor
            }

            return result;

        } catch (Throwable throwable) {
            // Capturar información del error
            recordBuilder
                    .success(false)
                    .errorType(throwable.getClass().getName())
                    .errorMessage(throwable.getMessage())
                    .errorStacktrace(getStackTrace(throwable, 4096));

            if (request != null) {
                recordBuilder.httpStatus(500); // Aproximado, se puede mejorar
            }

            throw throwable;

        } finally {
            // Calcular duración y persistir asíncronamente
            long duration = System.currentTimeMillis() - startTime;
            recordBuilder.durationMs(duration);

            CallHistoryRecord record = recordBuilder.build();
            asyncWriter.write(record);

            log.debug(
                    "Call history recorded: {} {} - Duration: {}ms - Success: {}",
                    record.getHttpMethod(),
                    record.getPath(),
                    duration,
                    record.getSuccess());
        }
    }

    /** Obtiene el correlation ID desde MDC o genera uno nuevo */
    private String getCorrelationId() {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isEmpty()) {
            // Intentar desde header HTTP
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null) {
                correlationId = request.getHeader("X-Correlation-ID");
            }
            // Si aún no hay, generar uno
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }
        }
        return correlationId;
    }

    /** Obtiene el trace ID desde MDC */
    private String getTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isEmpty()) {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null) {
                traceId = request.getHeader("X-Trace-ID");
            }
        }
        return traceId;
    }

    /** Obtiene el HttpServletRequest actual si está disponible */
    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Extrae la IP real del cliente considerando proxies */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Si hay múltiples IPs (proxy chain), tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /** Serializa los query parameters a JSON */
    private String serializeQueryParams(HttpServletRequest request) {
        try {
            Map<String, String[]> paramMap = request.getParameterMap();
            if (paramMap.isEmpty()) {
                return null;
            }
            Map<String, Object> simplified =
                    paramMap.entrySet().stream()
                            .collect(
                                    Collectors.toMap(
                                            Map.Entry::getKey,
                                            e ->
                                                    e.getValue().length == 1
                                                            ? e.getValue()[0]
                                                            : e.getValue()));
            return objectMapper.writeValueAsString(simplified);
        } catch (Exception e) {
            log.warn("Error serializing query params: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Serializa un payload (request/response) a JSON con masking y truncado
     *
     * @param payload Objeto a serializar
     * @param maskFields Campos a enmascarar
     * @param maxSize Tamaño máximo en caracteres
     * @return JSON serializado
     */
    private String serializePayload(Object payload, String[] maskFields, int maxSize) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            // Aplicar masking si hay campos sensibles
            if (maskFields != null && maskFields.length > 0) {
                json = maskSensitiveFields(json, maskFields);
            }

            // Truncar si excede el tamaño máximo
            if (json.length() > maxSize) {
                json = json.substring(0, maxSize) + "... [TRUNCATED]";
            }

            return json;
        } catch (Exception e) {
            log.warn("Error serializing payload: {}", e.getMessage());
            return "[Error serializing: " + e.getMessage() + "]";
        }
    }

    /**
     * Enmascara campos sensibles en un JSON Nota: Implementación simple basada en regex. Para
     * producción considerar un parser JSON completo.
     */
    private String maskSensitiveFields(String json, String[] maskFields) {
        String result = json;
        for (String field : maskFields) {
            // Pattern simple: "field":"value" -> "field":"***"
            result =
                    result.replaceAll(
                            "\"" + field + "\"\\s*:\\s*\"[^\"]*\"",
                            "\"" + field + "\":\"***MASKED***\"");
            // Pattern para valores sin comillas (números, booleanos)
            result =
                    result.replaceAll(
                            "\"" + field + "\"\\s*:\\s*[^,}\\]]+",
                            "\"" + field + "\":\"***MASKED***\"");
        }
        return result;
    }

    /** Extrae el stacktrace de una excepción limitado a un tamaño máximo */
    private String getStackTrace(Throwable throwable, int maxLength) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();

            if (stackTrace.length() > maxLength) {
                stackTrace = stackTrace.substring(0, maxLength) + "\n... [TRUNCATED]";
            }

            return stackTrace;
        } catch (Exception e) {
            return throwable.getMessage();
        }
    }
}
