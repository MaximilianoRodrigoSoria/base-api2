package com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.controller;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.*;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.api.CallHistoryApi;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.dto.CallHistoryResponse;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.inbound.web.mapper.CallHistoryDtoMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para consultar el historial de llamadas. Implementa CallHistoryApi para
 * documentación OpenAPI.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/call-history")
@RequiredArgsConstructor
public class CallHistoryController implements CallHistoryApi {

    private final ListCallHistoryCommand listCallHistoryCommand;
    private final FindByIdCommand findByIdCommand;
    private final FindByDateRangeCommand findByDateRangeCommand;
    private final FindByCorrelationIdCommand findByCorrelationIdCommand;
    private final FindByPathCommand findByPathCommand;
    private final FindBySuccessCommand findBySuccessCommand;
    private final CallHistoryDtoMapper dtoMapper;

    @GetMapping
    @Override
    public ResponseEntity<List<CallHistoryResponse>> listAll(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        log.info("Request GET /call-history?limit={}&offset={}", limit, offset);

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = listCallHistoryCommand.execute(limit, offset);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CallHistoryResponse> findById(@PathVariable Long id) {
        log.info("Request GET /call-history/{}", id);

        // Ejecutar caso de uso
        CallHistoryRecord record = findByIdCommand.execute(id);

        // Domain → DTO
        CallHistoryResponse response = dtoMapper.toResponse(record);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("Request GET /call-history/date-range?from={}&to={}", from, to);

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = findByDateRangeCommand.execute(from, to);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/correlation/{correlationId}")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByCorrelationId(
            @PathVariable String correlationId) {
        log.info("Request GET /call-history/correlation/{}", correlationId);

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = findByCorrelationIdCommand.execute(correlationId);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/path")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByPath(@RequestParam String path) {
        log.info("Request GET /call-history/path?path={}", path);

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = findByPathCommand.execute(path);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findBySuccess(@RequestParam boolean success) {
        log.info("Request GET /call-history/success?success={}", success);

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = findBySuccessCommand.execute(success);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/failures")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findFailures() {
        log.info("Request GET /call-history/failures");

        // Ejecutar caso de uso
        List<CallHistoryRecord> records = findBySuccessCommand.execute(false);

        // Domain → DTO
        List<CallHistoryResponse> response =
                records.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
