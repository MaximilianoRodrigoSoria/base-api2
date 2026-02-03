package com.ar.laboratory.baseapi2.callhistory.domain.exception;

/** Excepción de dominio cuando no se encuentra un registro de historial */
public class CallHistoryNotFoundException extends RuntimeException {

    public CallHistoryNotFoundException(String message) {
        super(message);
    }

    public CallHistoryNotFoundException(Long id) {
        super("Call history not found with id: " + id);
    }
}
