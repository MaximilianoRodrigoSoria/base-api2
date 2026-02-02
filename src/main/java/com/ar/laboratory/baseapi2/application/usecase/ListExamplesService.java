package com.ar.laboratory.baseapi2.application.usecase;

import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.in.ListExamplesUseCase;
import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.domain.model.Example;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación del caso de uso de listado de Examples */
@Slf4j
@Service
@RequiredArgsConstructor
public class ListExamplesService implements ListExamplesUseCase {

    private final ExampleRepositoryPort exampleRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ExampleResponse> listAll() {
        log.info("Listando todos los Examples");

        List<Example> examples = exampleRepositoryPort.findAll();

        log.info("Se encontraron {} Examples", examples.size());

        return examples.stream()
                .map(
                        example ->
                                ExampleResponse.builder()
                                        .id(example.getId())
                                        .name(example.getName())
                                        .dni(example.getDni())
                                        .build())
                .collect(Collectors.toList());
    }
}
