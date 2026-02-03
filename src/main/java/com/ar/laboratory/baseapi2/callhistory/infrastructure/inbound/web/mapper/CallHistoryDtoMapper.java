package com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.mapper;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.dto.CallHistoryResponse;
import org.springframework.stereotype.Component;

/** Mapper entre modelo de dominio y DTO de respuesta */
@Component
public class CallHistoryDtoMapper {

    public CallHistoryResponse toResponse(CallHistoryRecord record) {
        if (record == null) {
            return null;
        }

        return CallHistoryResponse.builder()
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
}
