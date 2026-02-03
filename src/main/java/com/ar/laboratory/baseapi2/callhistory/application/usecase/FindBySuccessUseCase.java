package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.FindBySuccessCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para buscar por éxito/fallo - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class FindBySuccessUseCase implements FindBySuccessCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryRecord> execute(boolean success) {
        log.debug("Finding call history by success: {}", success);
        return callHistoryRepository.findBySuccess(success);
    }
}
