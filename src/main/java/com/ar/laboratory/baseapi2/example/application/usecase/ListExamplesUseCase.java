package com.ar.laboratory.baseapi2.example.application.usecase;

import com.ar.laboratory.baseapi2.example.application.inbound.command.ListExamplesCommand;
import com.ar.laboratory.baseapi2.example.application.outbound.port.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.example.domain.model.Example;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para listar Examples - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class ListExamplesUseCase implements ListExamplesCommand {

    private final ExampleRepositoryPort exampleRepositoryPort;

    @Override
    public List<Example> execute() {
        log.info("Listando todos los Examples");
        List<Example> examples = exampleRepositoryPort.findAll();
        log.info("Se encontraron {} Examples", examples.size());
        return examples;
    }
}
