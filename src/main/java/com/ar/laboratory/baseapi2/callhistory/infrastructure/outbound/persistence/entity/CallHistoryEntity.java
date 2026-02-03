package com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/** Entidad JPA para la tabla call_history */
@Entity
@Table(
        name = "call_history",
        schema = "app",
        indexes = {
            @Index(name = "idx_call_history_created_at", columnList = "created_at"),
            @Index(name = "idx_call_history_path", columnList = "path"),
            @Index(name = "idx_call_history_success", columnList = "success"),
            @Index(name = "idx_call_history_http_status", columnList = "http_status"),
            @Index(name = "idx_call_history_correlation_id", columnList = "correlation_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "correlation_id", length = 128)
    private String correlationId;

    @Column(name = "trace_id", length = 128)
    private String traceId;

    @Column(name = "http_method", length = 16)
    private String httpMethod;

    @Column(name = "path", length = 512)
    private String path;

    @Column(name = "handler", length = 512)
    private String handler;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "error_type", length = 256)
    private String errorType;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_stacktrace", columnDefinition = "TEXT")
    private String errorStacktrace;
}
