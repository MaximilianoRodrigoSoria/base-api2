package com.ar.laboratory.baseapi2.shared.infrastructure.exception;

/** Excepción técnica de infraestructura */
public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
