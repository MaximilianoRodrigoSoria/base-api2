package com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.mapper;

import com.ar.laboratory.baseapi2.example.domain.model.Example;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.example.infrastructure.inbound.web.dto.ExampleResponse;
import org.springframework.stereotype.Component;

/** Mapper para convertir entre DTOs web y modelos de dominio */
@Component
public class ExampleDtoMapper {

    public ExampleResponse toResponse(Example domain) {
        if (domain == null) {
            return null;
        }

        return ExampleResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .dni(domain.getDni())
                .build();
    }

    public Example toDomain(CreateExampleRequest request) {
        if (request == null) {
            return null;
        }

        return Example.builder().name(request.getName()).dni(request.getDni()).build();
    }
}
