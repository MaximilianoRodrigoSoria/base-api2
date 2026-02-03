package com.ar.laboratory.baseapi2.callhistory.application.inbound.command;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;

/** Comando para buscar historial por ID */
public interface FindByIdCommand {

    CallHistoryRecord execute(Long id);
}
