package com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.controller;

import com.ar.laboratory.baseapi2.example.application.inbound.command.CreateExampleCommand;
import com.ar.laboratory.baseapi2.example.application.inbound.command.FindExampleByDniCommand;
import com.ar.laboratory.baseapi2.example.application.inbound.command.ListExamplesCommand;
import com.ar.laboratory.baseapi2.example.domain.model.Example;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.api.ExampleApi;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.mapper.ExampleDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
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

    private final CreateExampleCommand createExampleCommand;
    private final ListExamplesCommand listExamplesCommand;
    private final FindExampleByDniCommand findExampleByDniCommand;
    private final ExampleDtoMapper dtoMapper;

    @PostMapping
    @Override
    public ResponseEntity<ExampleResponse> create(
            @Valid @RequestBody CreateExampleRequest request) {
        log.info("Request POST /examples: {}", request);

        // DTO → Domain
        Example domain = dtoMapper.toDomain(request);

        // Ejecutar caso de uso
        Example result = createExampleCommand.execute(domain);

        // Domain → DTO
        ExampleResponse response = dtoMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ExampleResponse>> listAll() {
        log.info("Request GET /examples");

        // Ejecutar caso de uso
        List<Example> examples = listExamplesCommand.execute();

        // Domain → DTO
        List<ExampleResponse> response =
                examples.stream().map(dtoMapper::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dni/{dni}")
    @Override
    public ResponseEntity<ExampleResponse> findByDni(@PathVariable String dni) {
        log.info("Request GET /examples/dni/{}", dni);

        // Ejecutar caso de uso
        Example example = findExampleByDniCommand.execute(dni);

        // Domain → DTO
        ExampleResponse response = dtoMapper.toResponse(example);

        return ResponseEntity.ok(response);
    }
}
