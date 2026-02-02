package com.ar.laboratory.baseapi2.application.port.out;

import com.ar.laboratory.baseapi2.domain.model.Example;
import java.util.List;
import java.util.Optional;

/** Puerto de salida para operaciones de persistencia de Example */
public interface ExampleRepositoryPort {

    Example save(Example example);

    List<Example> findAll();

    Optional<Example> findById(Long id);

    Optional<Example> findByDni(String dni);

    boolean existsByDni(String dni);
}
