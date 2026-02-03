package com.ar.laboratory.baseapi2.callhistory.application.inbound.command;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;

/** Comando para listar historial de llamadas */
public interface ListCallHistoryCommand {

    List<CallHistoryRecord> execute(int limit, int offset);
}
