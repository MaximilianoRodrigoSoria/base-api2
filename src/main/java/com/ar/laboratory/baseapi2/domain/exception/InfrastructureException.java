package com.ar.laboratory.baseapi2.domain.exception;

/** Excepción para errores de infraestructura/persistencia */
public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
