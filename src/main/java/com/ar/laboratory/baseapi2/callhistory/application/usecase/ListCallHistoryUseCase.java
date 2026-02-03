package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.ListCallHistoryCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para listar historial de llamadas - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class ListCallHistoryUseCase implements ListCallHistoryCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryRecord> execute(int limit, int offset) {
        log.debug("Listing call history: limit={}, offset={}", limit, offset);
        return callHistoryRepository.findAll(limit, offset);
    }
}
