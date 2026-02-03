package com.ar.laboratory.baseapi2.application.port.out;

import com.ar.laboratory.baseapi2.domain.model.CallHistoryRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para persistencia del historial de llamadas */
public interface CallHistoryRepositoryPort {

    /**
     * Guarda un registro de historial de llamada
     *
     * @param record Registro a guardar
     * @return Registro guardado con ID generado
     */
    CallHistoryRecord save(CallHistoryRecord record);

    /**
     * Busca un registro por ID
     *
     * @param id ID del registro
     * @return Optional con el registro si existe
     */
    Optional<CallHistoryRecord> findById(Long id);

    /**
     * Lista registros paginados
     *
     * @param limit Cantidad máxima de registros
     * @param offset Offset para paginación
     * @return Lista de registros
     */
    List<CallHistoryRecord> findAll(int limit, int offset);

    /**
     * Busca registros por rango de fechas
     *
     * @param from Fecha desde
     * @param to Fecha hasta
     * @return Lista de registros en el rango
     */
    List<CallHistoryRecord> findByDateRange(LocalDateTime from, LocalDateTime to);

    /**
     * Busca registros por correlation ID
     *
     * @param correlationId ID de correlación
     * @return Lista de registros con ese correlation ID
     */
    List<CallHistoryRecord> findByCorrelationId(String correlationId);

    /**
     * Busca registros por path
     *
     * @param path Path del endpoint
     * @return Lista de registros para ese path
     */
    List<CallHistoryRecord> findByPath(String path);

    /**
     * Busca registros por estado de éxito
     *
     * @param success true para llamadas exitosas, false para fallidas
     * @return Lista de registros según el estado
     */
    List<CallHistoryRecord> findBySuccess(boolean success);
}
