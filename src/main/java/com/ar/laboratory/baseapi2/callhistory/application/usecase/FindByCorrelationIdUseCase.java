package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.FindByCorrelationIdCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para buscar por correlation ID - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class FindByCorrelationIdUseCase implements FindByCorrelationIdCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryRecord> execute(String correlationId) {
        log.debug("Finding call history by correlationId: {}", correlationId);
        return callHistoryRepository.findByCorrelationId(correlationId);
    }
}
