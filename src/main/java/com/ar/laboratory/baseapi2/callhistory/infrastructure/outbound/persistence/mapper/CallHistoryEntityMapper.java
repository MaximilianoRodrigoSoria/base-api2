package com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.mapper;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.entity.CallHistoryEntity;
import org.springframework.stereotype.Component;

/** Mapper entre modelo de dominio y entidad JPA */
@Component
public class CallHistoryEntityMapper {

    public CallHistoryEntity toEntity(CallHistoryRecord record) {
        if (record == null) {
            return null;
        }

        return CallHistoryEntity.builder()
                .id(record.getId())
                .createdAt(record.getCreatedAt())
                .correlationId(record.getCorrelationId())
                .traceId(record.getTraceId())
                .httpMethod(record.getHttpMethod())
                .path(record.getPath())
                .handler(record.getHandler())
                .httpStatus(record.getHttpStatus())
                .success(record.getSuccess())
                .durationMs(record.getDurationMs())
                .clientIp(record.getClientIp())
                .userAgent(record.getUserAgent())
                .userId(record.getUserId())
                .queryParams(record.getQueryParams())
                .requestBody(record.getRequestBody())
                .responseBody(record.getResponseBody())
                .errorType(record.getErrorType())
                .errorMessage(record.getErrorMessage())
                .errorStacktrace(record.getErrorStacktrace())
                .build();
    }

    public CallHistoryRecord toDomain(CallHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return CallHistoryRecord.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .correlationId(entity.getCorrelationId())
                .traceId(entity.getTraceId())
                .httpMethod(entity.getHttpMethod())
                .path(entity.getPath())
                .handler(entity.getHandler())
                .httpStatus(entity.getHttpStatus())
                .success(entity.getSuccess())
                .durationMs(entity.getDurationMs())
                .clientIp(entity.getClientIp())
                .userAgent(entity.getUserAgent())
                .userId(entity.getUserId())
                .queryParams(entity.getQueryParams())
                .requestBody(entity.getRequestBody())
                .responseBody(entity.getResponseBody())
                .errorType(entity.getErrorType())
                .errorMessage(entity.getErrorMessage())
                .errorStacktrace(entity.getErrorStacktrace())
                .build();
    }
}
