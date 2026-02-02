package com.ar.laboratory.baseapi2.infrastructure.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio JPA para Example */
@Repository
public interface ExampleJpaRepository extends JpaRepository<ExampleJpaEntity, Long> {

    Optional<ExampleJpaEntity> findByDni(String dni);

    boolean existsByDni(String dni);
}
