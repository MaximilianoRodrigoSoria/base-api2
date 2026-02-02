package com.ar.laboratory.baseapi2.application.usecase;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.in.CreateExampleUseCase;
import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.domain.exception.ExampleAlreadyExistsException;
import com.ar.laboratory.baseapi2.domain.model.Example;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación del caso de uso de creación de Example */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateExampleService implements CreateExampleUseCase {

    private final ExampleRepositoryPort exampleRepositoryPort;

    @Override
    @Transactional
    public ExampleResponse create(CreateExampleRequest request) {
        log.info("Creando Example con DNI: {}", request.getDni());

        // Verificar si ya existe un Example con el mismo DNI
        if (exampleRepositoryPort.existsByDni(request.getDni())) {
            log.warn("Ya existe un Example con DNI: {}", request.getDni());
            throw new ExampleAlreadyExistsException(request.getDni());
        }

        // Crear el modelo de dominio
        Example example = Example.builder().name(request.getName()).dni(request.getDni()).build();

        // Guardar
        Example savedExample = exampleRepositoryPort.save(example);

        log.info("Example creado exitosamente con ID: {}", savedExample.getId());

        // Mapear a DTO de respuesta
        return ExampleResponse.builder()
                .id(savedExample.getId())
                .name(savedExample.getName())
                .dni(savedExample.getDni())
                .build();
    }
}
