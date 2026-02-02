package com.ar.laboratory.baseapi2.application.port.in;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;

/** Puerto de entrada para crear un Example */
public interface CreateExampleUseCase {

    ExampleResponse create(CreateExampleRequest request);
}
