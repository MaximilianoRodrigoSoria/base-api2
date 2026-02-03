package com.ar.laboratory.baseapi2.application.usecase;

import com.ar.laboratory.baseapi2.application.dto.CallHistoryResponse;
import com.ar.laboratory.baseapi2.application.port.in.ListCallHistoryUseCase;
import com.ar.laboratory.baseapi2.application.port.out.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.domain.exception.ExampleNotFoundException;
import com.ar.laboratory.baseapi2.domain.model.CallHistoryRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Servicio para listar y consultar el historial de llamadas */
@Slf4j
@Service
@RequiredArgsConstructor
public class ListCallHistoryService implements ListCallHistoryUseCase {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryResponse> listAll(int limit, int offset) {
        log.debug("Listing call history: limit={}, offset={}", limit, offset);
        List<CallHistoryRecord> records = callHistoryRepository.findAll(limit, offset);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryResponse> findByDateRange(LocalDateTime from, LocalDateTime to) {
        log.debug("Finding call history by date range: from={}, to={}", from, to);
        List<CallHistoryRecord> records = callHistoryRepository.findByDateRange(from, to);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryResponse> findByCorrelationId(String correlationId) {
        log.debug("Finding call history by correlationId: {}", correlationId);
        List<CallHistoryRecord> records = callHistoryRepository.findByCorrelationId(correlationId);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryResponse> findByPath(String path) {
        log.debug("Finding call history by path: {}", path);
        List<CallHistoryRecord> records = callHistoryRepository.findByPath(path);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryResponse> findBySuccess(boolean success) {
        log.debug("Finding call history by success: {}", success);
        List<CallHistoryRecord> records = callHistoryRepository.findBySuccess(success);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CallHistoryResponse findById(Long id) {
        log.debug("Finding call history by id: {}", id);
        CallHistoryRecord record =
                callHistoryRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ExampleNotFoundException(
                                                "Call history not found with id: " + id));
        return toResponse(record);
    }

    private CallHistoryResponse toResponse(CallHistoryRecord record) {
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
