package com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.adapter;

import com.ar.laboratory.baseapi2.example.application.outbound.port.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.example.domain.model.Example;
import com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.entity.ExampleEntity;
import com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.mapper.ExampleEntityMapper;
import com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.repository.ExampleJpaRepository;
import com.ar.laboratory.baseapi2.shared.infrastructure.exception.InfrastructureException;
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
    private final ExampleEntityMapper entityMapper;

    @Override
    public Example save(Example example) {
        try {
            ExampleEntity entity = entityMapper.toEntity(example);
            ExampleEntity saved = jpaRepository.save(entity);
            return entityMapper.toDomain(saved);
        } catch (Exception e) {
            log.error("Error guardando Example: {}", example, e);
            throw new InfrastructureException("Error guardando Example", e);
        }
    }

    @Override
    public List<Example> findAll() {
        try {
            return jpaRepository.findAll().stream()
                    .map(entityMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listando Examples", e);
            throw new InfrastructureException("Error listando Examples", e);
        }
    }

    @Override
    public Optional<Example> findById(Long id) {
        try {
            return jpaRepository.findById(id).map(entityMapper::toDomain);
        } catch (Exception e) {
            log.error("Error buscando Example por ID: {}", id, e);
            throw new InfrastructureException("Error buscando Example por ID", e);
        }
    }

    @Override
    public Optional<Example> findByDni(String dni) {
        try {
            return jpaRepository.findByDni(dni).map(entityMapper::toDomain);
        } catch (Exception e) {
            log.error("Error buscando Example por DNI: {}", dni, e);
            throw new InfrastructureException("Error buscando Example por DNI", e);
        }
    }

    @Override
    public boolean existsByDni(String dni) {
        try {
            return jpaRepository.existsByDni(dni);
        } catch (Exception e) {
            log.error("Error verificando existencia de DNI: {}", dni, e);
            throw new InfrastructureException("Error verificando existencia de DNI", e);
        }
    }
}
