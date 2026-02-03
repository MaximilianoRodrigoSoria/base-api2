package com.ar.laboratory.baseapi2.example.domain.exception;

/** Excepción lanzada cuando no se encuentra un Example */
public class ExampleNotFoundException extends RuntimeException {

    public ExampleNotFoundException(String message) {
        super(message);
    }
}
