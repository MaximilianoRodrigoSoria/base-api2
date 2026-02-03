package com.ar.laboratory.baseapi2.example.application.inbound.command;

import com.ar.laboratory.baseapi2.example.domain.model.Example;
import java.util.List;

/** Puerto de entrada para listar todos los Examples */
public interface ListExamplesCommand {

    List<Example> execute();
}
