package com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.repository;

import com.ar.laboratory.baseapi2.example.infrastructure.outbound.persistence.entity.ExampleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio JPA para ExampleEntity */
public interface ExampleJpaRepository extends JpaRepository<ExampleEntity, Long> {

    Optional<ExampleEntity> findByDni(String dni);

    boolean existsByDni(String dni);
}
