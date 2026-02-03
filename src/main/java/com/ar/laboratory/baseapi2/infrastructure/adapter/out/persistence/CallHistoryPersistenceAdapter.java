package com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence;

import com.ar.laboratory.baseapi2.application.port.out.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence.entity.CallHistoryEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia para el historial de llamadas */
@Component
@RequiredArgsConstructor
public class CallHistoryPersistenceAdapter implements CallHistoryRepositoryPort {

    private final CallHistoryJpaRepository jpaRepository;

    @Override
    public CallHistoryRecord save(CallHistoryRecord record) {
        CallHistoryEntity entity = toEntity(record);
        CallHistoryEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CallHistoryRecord> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CallHistoryRecord> findAll(int limit, int offset) {
        return jpaRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(offset / limit, limit))
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByCorrelationId(String correlationId) {
        return jpaRepository.findByCorrelationIdOrderByCreatedAtDesc(correlationId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByPath(String path) {
        return jpaRepository.findByPathOrderByCreatedAtDesc(path).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findBySuccess(boolean success) {
        return jpaRepository.findBySuccessOrderByCreatedAtDesc(success).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private CallHistoryEntity toEntity(CallHistoryRecord record) {
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

    private CallHistoryRecord toDomain(CallHistoryEntity entity) {
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
