package com.ar.laboratory.baseapi2.callhistory.application.usecase;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.FindByDateRangeCommand;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Caso de uso para buscar por rango de fechas - POJO puro sin framework */
@Slf4j
@RequiredArgsConstructor
public class FindByDateRangeUseCase implements FindByDateRangeCommand {

    private final CallHistoryRepositoryPort callHistoryRepository;

    @Override
    public List<CallHistoryRecord> execute(LocalDateTime from, LocalDateTime to) {
        log.debug("Finding call history by date range: from={}, to={}", from, to);
        return callHistoryRepository.findByDateRange(from, to);
    }
}
