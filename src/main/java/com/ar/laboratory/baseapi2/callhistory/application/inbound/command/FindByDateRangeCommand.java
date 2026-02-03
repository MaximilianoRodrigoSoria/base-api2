package com.ar.laboratory.baseapi2.callhistory.application.inbound.command;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.time.LocalDateTime;
import java.util.List;

/** Comando para buscar historial por rango de fechas */
public interface FindByDateRangeCommand {

    List<CallHistoryRecord> execute(LocalDateTime from, LocalDateTime to);
}
