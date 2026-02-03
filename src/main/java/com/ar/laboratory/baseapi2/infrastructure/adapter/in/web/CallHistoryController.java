package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web;

import com.ar.laboratory.baseapi2.application.dto.CallHistoryResponse;
import com.ar.laboratory.baseapi2.application.port.in.ListCallHistoryUseCase;
import com.ar.laboratory.baseapi2.infrastructure.adapter.in.web.api.CallHistoryApi;
import java.time.LocalDateTime;
import java.util.List;
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

    private final ListCallHistoryUseCase listCallHistoryUseCase;

    @GetMapping
    @Override
    public ResponseEntity<List<CallHistoryResponse>> listAll(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        log.info("Request GET /call-history?limit={}&offset={}", limit, offset);
        List<CallHistoryResponse> response = listCallHistoryUseCase.listAll(limit, offset);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CallHistoryResponse> findById(@PathVariable Long id) {
        log.info("Request GET /call-history/{}", id);
        CallHistoryResponse response = listCallHistoryUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("Request GET /call-history/date-range?from={}&to={}", from, to);
        List<CallHistoryResponse> response = listCallHistoryUseCase.findByDateRange(from, to);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/correlation/{correlationId}")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByCorrelationId(
            @PathVariable String correlationId) {
        log.info("Request GET /call-history/correlation/{}", correlationId);
        List<CallHistoryResponse> response =
                listCallHistoryUseCase.findByCorrelationId(correlationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/path")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findByPath(@RequestParam String path) {
        log.info("Request GET /call-history/path?path={}", path);
        List<CallHistoryResponse> response = listCallHistoryUseCase.findByPath(path);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findBySuccess(@RequestParam boolean success) {
        log.info("Request GET /call-history/success?success={}", success);
        List<CallHistoryResponse> response = listCallHistoryUseCase.findBySuccess(success);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failures")
    @Override
    public ResponseEntity<List<CallHistoryResponse>> findFailures() {
        log.info("Request GET /call-history/failures");
        List<CallHistoryResponse> response = listCallHistoryUseCase.findBySuccess(false);
        return ResponseEntity.ok(response);
    }
}
