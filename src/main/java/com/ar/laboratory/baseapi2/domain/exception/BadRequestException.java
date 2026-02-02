package com.ar.laboratory.baseapi2.domain.exception;

/** Excepción para requests inválidos */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
