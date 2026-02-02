package com.ar.laboratory.baseapi2.domain.exception;

/** Excepción cuando no se encuentra un Example */
public class ExampleNotFoundException extends RuntimeException {

    public ExampleNotFoundException(String message) {
        super(message);
    }

    public ExampleNotFoundException(Long id) {
        super("Example no encontrado con ID: " + id);
    }
}
