package com.ar.laboratory.baseapi2.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Modelo de dominio para registrar el historial de llamadas a endpoints/métodos. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistoryRecord {

    private Long id;

    private LocalDateTime createdAt;

    private String correlationId;

    private String traceId;

    private String httpMethod;

    private String path;

    private String handler;

    private Integer httpStatus;

    private Boolean success;

    private Long durationMs;

    private String clientIp;

    private String userAgent;

    private String userId;

    private String queryParams;

    private String requestBody;

    private String responseBody;

    private String errorType;

    private String errorMessage;

    private String errorStacktrace;
}
