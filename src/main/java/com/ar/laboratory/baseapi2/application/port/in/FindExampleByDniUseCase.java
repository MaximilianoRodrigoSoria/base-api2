package com.ar.laboratory.baseapi2.application.port.in;

import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;

/** Caso de uso para buscar un Example por DNI */
public interface FindExampleByDniUseCase {

    /**
     * Busca un Example por su DNI
     *
     * @param dni DNI del Example a buscar
     * @return Example encontrado
     * @throws com.ar.laboratory.baseapi2.domain.exception.ExampleNotFoundException si no se
     *     encuentra
     */
    ExampleResponse findByDni(String dni);
}
