package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.FindByPathCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para buscar por path - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class FindByPathUseCase implements FindByPathCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryRecord> execute(String path) {
        log.debug("Finding call history by path: {}", path);
        return callHistoryRepository.findByPath(path);
    }
}
