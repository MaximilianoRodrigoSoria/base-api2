package com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.adapter;

import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.domain.model.CallHistoryRecord;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.entity.CallHistoryEntity;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.mapper.CallHistoryEntityMapper;
import com.ar.laboratory.baseapi2.callhistory.infrastructure.outbound.persistence.repository.CallHistoryJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia para el historial de llamadas */
@Component
@RequiredArgsConstructor
public class CallHistoryPersistenceAdapter implements CallHistoryRepositoryPort {

    private final CallHistoryJpaRepository jpaRepository;
    private final CallHistoryEntityMapper entityMapper;

    @Override
    public CallHistoryRecord save(CallHistoryRecord record) {
        CallHistoryEntity entity = entityMapper.toEntity(record);
        CallHistoryEntity saved = jpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<CallHistoryRecord> findById(Long id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public List<CallHistoryRecord> findAll(int limit, int offset) {
        return jpaRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(offset / limit, limit))
                .stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByCorrelationId(String correlationId) {
        return jpaRepository.findByCorrelationIdOrderByCreatedAtDesc(correlationId).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findByPath(String path) {
        return jpaRepository.findByPathOrderByCreatedAtDesc(path).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CallHistoryRecord> findBySuccess(boolean success) {
        return jpaRepository.findBySuccessOrderByCreatedAtDesc(success).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
