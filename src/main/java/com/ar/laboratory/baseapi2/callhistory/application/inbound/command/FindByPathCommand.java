package com.ar.laboratory.baseapi2.callhistory.application.inbound.command;

import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;

/** Comando para buscar historial por path */
public interface FindByPathCommand {

    List<CallHistoryRecord> execute(String path);
}
