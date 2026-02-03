package com.ar.laboratory.baseapi2.callhistory.application.inbound.command;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;

/** Comando para buscar historial por éxito/fallo */
public interface FindBySuccessCommand {

    List<CallHistoryRecord> execute(boolean success);
}
