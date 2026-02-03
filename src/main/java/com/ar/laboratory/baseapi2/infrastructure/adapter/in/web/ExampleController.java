package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.in.CreateExampleUseCase;
import com.ar.laboratory.baseapi2.application.port.in.FindExampleByDniUseCase;
import com.ar.laboratory.baseapi2.application.port.in.ListExamplesUseCase;
import com.ar.laboratory.baseapi2.infrastructure.adapter.in.web.api.ExampleApi;
import com.ar.laboratory.baseapi2.infrastructure.annotation.CallHistory;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller REST para Examples. Implementa ExampleApi para documentación OpenAPI. */
@Slf4j
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
public class ExampleController implements ExampleApi {

    private final CreateExampleUseCase createExampleUseCase;
    private final ListExamplesUseCase listExamplesUseCase;
    private final FindExampleByDniUseCase findExampleByDniUseCase;

    @CallHistory(
            action = "CREATE_EXAMPLE",
            logRequest = true,
            logResponse = true,
            maskFields = {"password", "token"})
    @PostMapping
    @Override
    public ResponseEntity<ExampleResponse> create(
            @Valid @RequestBody CreateExampleRequest request) {
        log.info("Request POST /examples: {}", request);
        ExampleResponse response = createExampleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ExampleResponse>> listAll() {
        log.info("Request GET /examples");
        List<ExampleResponse> response = listExamplesUseCase.listAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dni/{dni}")
    @Override
    public ResponseEntity<ExampleResponse> findByDni(@PathVariable String dni) {
        log.info("Request GET /examples/dni/{}", dni);
        ExampleResponse response = findExampleByDniUseCase.findByDni(dni);
        return ResponseEntity.ok(response);
    }
}
