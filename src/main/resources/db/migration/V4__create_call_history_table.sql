-- Crear tabla para historial de llamadas a endpoints
-- Permite auditoría completa de invocaciones con metadatos, payload y errores

CREATE TABLE IF NOT EXISTS app.call_history (
    id                BIGSERIAL PRIMARY KEY,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    correlation_id    VARCHAR(128),
    trace_id          VARCHAR(128),

    http_method       VARCHAR(16),
    path              VARCHAR(512),
    handler           VARCHAR(512),

    http_status       INTEGER,
    success           BOOLEAN NOT NULL DEFAULT TRUE,
    duration_ms       BIGINT,

    client_ip         VARCHAR(64),
    user_agent        VARCHAR(512),
    user_id           VARCHAR(128),

    query_params      TEXT,
    request_body      TEXT,
    response_body     TEXT,

    error_type        VARCHAR(256),
    error_message     TEXT,
    error_stacktrace  TEXT
);

-- Índices recomendados para búsquedas comunes
CREATE INDEX IF NOT EXISTS idx_call_history_created_at
    ON app.call_history (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_call_history_path
    ON app.call_history (path);

CREATE INDEX IF NOT EXISTS idx_call_history_success
    ON app.call_history (success);

CREATE INDEX IF NOT EXISTS idx_call_history_http_status
    ON app.call_history (http_status);

CREATE INDEX IF NOT EXISTS idx_call_history_correlation_id
    ON app.call_history (correlation_id);

-- Comentarios para documentar la tabla
COMMENT ON TABLE app.call_history IS 'Historial de llamadas a endpoints para auditoría y debugging';
COMMENT ON COLUMN app.call_history.correlation_id IS 'ID de correlación para rastrear llamadas relacionadas';
COMMENT ON COLUMN app.call_history.trace_id IS 'ID de tracing distribuido';
COMMENT ON COLUMN app.call_history.handler IS 'Clase#método que procesó la llamada';
COMMENT ON COLUMN app.call_history.duration_ms IS 'Duración de la llamada en milisegundos';
COMMENT ON COLUMN app.call_history.success IS 'Indica si la llamada fue exitosa o falló';
