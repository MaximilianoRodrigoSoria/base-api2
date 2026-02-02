package com.ar.laboratory.baseapi2.domain.exception;

/** Excepción cuando ya existe un Example con el mismo DNI */
public class ExampleAlreadyExistsException extends RuntimeException {

    public ExampleAlreadyExistsException(String dni) {
        super("Ya existe un Example con DNI: " + dni);
    }
}
