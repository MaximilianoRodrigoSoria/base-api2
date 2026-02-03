package com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence;

import com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence.entity.CallHistoryEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio JPA para CallHistoryEntity */
@Repository
public interface CallHistoryJpaRepository extends JpaRepository<CallHistoryEntity, Long> {

    List<CallHistoryEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to);

    List<CallHistoryEntity> findByCorrelationIdOrderByCreatedAtDesc(String correlationId);

    List<CallHistoryEntity> findByPathOrderByCreatedAtDesc(String path);

    List<CallHistoryEntity> findBySuccessOrderByCreatedAtDesc(boolean success);

    List<CallHistoryEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
