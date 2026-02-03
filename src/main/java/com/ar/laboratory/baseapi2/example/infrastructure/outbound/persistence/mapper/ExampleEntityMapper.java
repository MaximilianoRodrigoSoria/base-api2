package com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.mapper;

import com.ar.laboratory.baseapi2.example.domain.model.Example;
import com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.entity.ExampleEntity;
import org.springframework.stereotype.Component;

/** Mapper para convertir entre entidades JPA y modelos de dominio */
@Component
public class ExampleEntityMapper {

    public Example toDomain(ExampleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Example.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dni(entity.getDni())
                .build();
    }

    public ExampleEntity toEntity(Example domain) {
        if (domain == null) {
            return null;
        }

        return ExampleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .dni(domain.getDni())
                .build();
    }
}
