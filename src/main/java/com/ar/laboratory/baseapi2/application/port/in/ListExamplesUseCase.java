package com.ar.laboratory.baseapi2.application.port.in;

import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import java.util.List;

/** Puerto de entrada para listar Examples */
public interface ListExamplesUseCase {

    List<ExampleResponse> listAll();
}
