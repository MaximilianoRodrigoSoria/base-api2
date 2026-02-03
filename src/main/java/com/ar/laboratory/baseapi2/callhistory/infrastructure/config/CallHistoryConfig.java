package com.ar.laboratory.baseapi2.callhistory.infrastructure.config;

import com.ar.laboratory.baseapi2.callhistory.application.inbound.command.*;
import com.ar.laboratory.baseapi2.callhistory.application.outbound.port.CallHistoryRepositoryPort;
import com.ar.laboratory.baseapi2.callhistory.application.usecase.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para el feature CallHistory
 *
 * <p>Aquí se realiza el wiring de: - Commands (puertos de entrada) - UseCases (implementaciones
 * puras) - Decoración de caché (preocupaciones de infraestructura)
 */
@Configuration
public class CallHistoryConfig {

    /**
     * Bean para listar historial de llamadas
     *
     * <p>Aplica caché de lectura en infraestructura
     */
    @Bean
    @Cacheable(value = "callHistoryCache", key = "'listAll:' + #limit + ':' + #offset")
    public ListCallHistoryCommand listCallHistoryCommand(CallHistoryRepositoryPort repositoryPort) {
        return new ListCallHistoryUseCase(repositoryPort);
    }

    /** Bean para buscar historial por ID */
    @Bean
    public FindByIdCommand findByIdCommand(CallHistoryRepositoryPort repositoryPort) {
        return new FindByIdUseCase(repositoryPort);
    }

    /** Bean para buscar historial por rango de fechas */
    @Bean
    public FindByDateRangeCommand findByDateRangeCommand(CallHistoryRepositoryPort repositoryPort) {
        return new FindByDateRangeUseCase(repositoryPort);
    }

    /** Bean para buscar historial por correlation ID */
    @Bean
    public FindByCorrelationIdCommand findByCorrelationIdCommand(
            CallHistoryRepositoryPort repositoryPort) {
        return new FindByCorrelationIdUseCase(repositoryPort);
    }

    /** Bean para buscar historial por path */
    @Bean
    public FindByPathCommand findByPathCommand(CallHistoryRepositoryPort repositoryPort) {
        return new FindByPathUseCase(repositoryPort);
    }

    /** Bean para buscar historial por éxito/fallo */
    @Bean
    public FindBySuccessCommand findBySuccessCommand(CallHistoryRepositoryPort repositoryPort) {
        return new FindBySuccessUseCase(repositoryPort);
    }
}
