package com.ar.laboratory.baseapi2.application.port.in;

import com.ar.laboratory.baseapi2.application.dto.CallHistoryResponse;
import java.time.LocalDateTime;
import java.util.List;

/** Puerto de entrada para listar el historial de llamadas */
public interface ListCallHistoryUseCase {

    /**
     * Lista todos los registros de historial con paginación
     *
     * @param limit Cantidad máxima de registros
     * @param offset Offset para paginación
     * @return Lista de registros
     */
    List<CallHistoryResponse> listAll(int limit, int offset);

    /**
     * Busca registros por rango de fechas
     *
     * @param from Fecha desde
     * @param to Fecha hasta
     * @return Lista de registros en el rango
     */
    List<CallHistoryResponse> findByDateRange(LocalDateTime from, LocalDateTime to);

    /**
     * Busca registros por correlation ID
     *
     * @param correlationId ID de correlación
     * @return Lista de registros con ese correlation ID
     */
    List<CallHistoryResponse> findByCorrelationId(String correlationId);

    /**
     * Busca registros por path
     *
     * @param path Path del endpoint
     * @return Lista de registros para ese path
     */
    List<CallHistoryResponse> findByPath(String path);

    /**
     * Busca registros por estado de éxito
     *
     * @param success true para llamadas exitosas, false para fallidas
     * @return Lista de registros según el estado
     */
    List<CallHistoryResponse> findBySuccess(boolean success);

    /**
     * Busca un registro por ID
     *
     * @param id ID del registro
     * @return Registro encontrado
     */
    CallHistoryResponse findById(Long id);
}
