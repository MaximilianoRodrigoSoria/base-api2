package com.ar.laboratory.baseapi2.shared.infrastructure.history;

import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para escritura asíncrona del historial de llamadas. Persiste los registros de forma
 * asíncrona para no impactar el tiempo de respuesta del endpoint principal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallHistoryAsyncWriter {

    private final CallHistoryRepositoryPort callHistoryRepository;

    /**
     * Escribe un registro de historial de forma asíncrona. Si falla, registra el error pero no
     * propaga la excepción (tolerancia a fallos).
     *
     * @param record Registro a persistir
     */
    @Async
    public void write(CallHistoryRecord record) {
        try {
            callHistoryRepository.save(record);
            log.debug(
                    "Call history saved successfully: {} {} [{}ms]",
                    record.getHttpMethod(),
                    record.getPath(),
                    record.getDurationMs());
        } catch (Exception e) {
            // Tolerancia a fallos: si falla el guardado, no debe romper el flujo principal
            log.error(
                    "Failed to save call history for {} {}: {}",
                    record.getHttpMethod(),
                    record.getPath(),
                    e.getMessage(),
                    e);
        }
    }
}
