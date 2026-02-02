package com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence;

import com.ar.laboratory.baseapi2.application.port.out.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.domain.exception.InfrastructureException;
import com.ar.laboratory.baseapi2.domain.model.Example;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia para Example */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExamplePersistenceAdapter implements ExampleRepositoryPort {

    private final ExampleJpaRepository jpaRepository;

    @Override
    public Example save(Example example) {
        try {
            log.debug("Guardando Example en BD: {}", example);

            ExampleJpaEntity entity =
                    ExampleJpaEntity.builder()
                            .id(example.getId())
                            .name(example.getName())
                            .dni(example.getDni())
                            .build();

            ExampleJpaEntity savedEntity = jpaRepository.save(entity);

            return mapToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error guardando Example", e);
            throw new InfrastructureException("Error al guardar Example", e);
        }
    }

    @Override
    public List<Example> findAll() {
        try {
            log.debug("Buscando todos los Examples en BD");

            return jpaRepository.findAll().stream()
                    .map(this::mapToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listando Examples", e);
            throw new InfrastructureException("Error al listar Examples", e);
        }
    }

    @Override
    public Optional<Example> findById(Long id) {
        try {
            log.debug("Buscando Example por ID: {}", id);

            return jpaRepository.findById(id).map(this::mapToDomain);
        } catch (Exception e) {
            log.error("Error buscando Example por ID", e);
            throw new InfrastructureException("Error al buscar Example por ID", e);
        }
    }

    @Override
    public Optional<Example> findByDni(String dni) {
        try {
            log.debug("Buscando Example por DNI: {}", dni);

            return jpaRepository.findByDni(dni).map(this::mapToDomain);
        } catch (Exception e) {
            log.error("Error buscando Example por DNI", e);
            throw new InfrastructureException("Error al buscar Example por DNI", e);
        }
    }

    @Override
    public boolean existsByDni(String dni) {
        try {
            log.debug("Verificando existencia de Example con DNI: {}", dni);

            return jpaRepository.existsByDni(dni);
        } catch (Exception e) {
            log.error("Error verificando existencia de Example", e);
            throw new InfrastructureException("Error al verificar existencia de Example", e);
        }
    }

    private Example mapToDomain(ExampleJpaEntity entity) {
        return Example.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dni(entity.getDni())
                .build();
    }
}
