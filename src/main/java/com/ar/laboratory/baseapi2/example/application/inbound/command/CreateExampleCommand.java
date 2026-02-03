package com.ar.laboratory.baseapi2.example.application.inbound.command;

import com.ar.laboratory.baseapi2.example.domain.model.Example;

/** Puerto de entrada para crear un Example */
public interface CreateExampleCommand {

    Example execute(Example example);
}
