package com.ar.laboratory.baseapi2.callhistory.application.outbound.port;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para persistencia de historial de llamadas */
public interface CallHistoryRepositoryPort {

    CallHistoryRecord save(CallHistoryRecord record);

    List<CallHistoryRecord> findAll(int limit, int offset);

    List<CallHistoryRecord> findByDateRange(LocalDateTime from, LocalDateTime to);

    List<CallHistoryRecord> findByCorrelationId(String correlationId);

    List<CallHistoryRecord> findByPath(String path);

    List<CallHistoryRecord> findBySuccess(boolean success);

    Optional<CallHistoryRecord> findById(Long id);
}
