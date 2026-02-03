package com.ar.laboratory.baseapi2.example.application.inbound.command;

import com.ar.laboratory.baseapi2.example.domain.model.Example;

/** Puerto de entrada para buscar un Example por DNI */
public interface FindExampleByDniCommand {

    Example execute(String dni);
}
