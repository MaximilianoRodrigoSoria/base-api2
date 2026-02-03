package com.ar.laboratory.baseapi2.example.domain.exception;

/** Excepción lanzada cuando se intenta crear un Example que ya existe */
public class ExampleAlreadyExistsException extends RuntimeException {

    public ExampleAlreadyExistsException(String dni) {
        super("Ya existe un Example con DNI: " + dni);
    }
}
