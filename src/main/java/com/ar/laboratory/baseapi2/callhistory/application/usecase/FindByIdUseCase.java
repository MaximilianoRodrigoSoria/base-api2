package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.FindByIdCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.exception.CallHistoryNotFoundException;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para buscar por ID - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class FindByIdUseCase implements FindByIdCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public CallHistoryRecord execute(Long id) {
        log.debug("Finding call history by id: {}", id);
        return callHistoryRepository
                .findById(id)
                .orElseThrow(() -> new CallHistoryNotFoundException(id));
    }
}
