package com.ar.laboratory.baseapi2.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO de respuesta para Example */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleResponse {

    private Long id;
    private String name;
    private String dni;
}
