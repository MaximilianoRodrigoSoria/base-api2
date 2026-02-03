package com.ar.laboratory.baseapi2.application.usecase;

import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.in.FindExampleByDniUseCase;
import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.domain.exception.ExampleNotFoundException;
import com.ar.laboratory.baseapi2.domain.model.Example;
import com.ar.laboratory.baseapi2.infrastructure.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación del caso de uso de búsqueda de Example por DNI */
@Slf4j
@Service
@RequiredArgsConstructor
public class FindExampleByDniService implements FindExampleByDniUseCase {

    private final ExampleRepositoryPort exampleRepositoryPort;

    @Override
    @Cacheable(value = CacheConfig.EXAMPLES_BY_DNI, key = "#dni")
    @Transactional(readOnly = true)
    public ExampleResponse findByDni(String dni) {
        log.info("Buscando Example por DNI: {}", dni);

        Example example =
                exampleRepositoryPort
                        .findByDni(dni)
                        .orElseThrow(
                                () -> {
                                    log.error("Example no encontrado con DNI: {}", dni);
                                    return new ExampleNotFoundException(
                                            "Example no encontrado con DNI: " + dni);
                                });

        log.info("Example encontrado: {}", example.getName());

        return ExampleResponse.builder()
                .id(example.getId())
                .name(example.getName())
                .dni(example.getDni())
                .build();
    }
}
