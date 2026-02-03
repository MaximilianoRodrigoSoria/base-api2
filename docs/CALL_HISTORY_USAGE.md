# Sistema de Historial de Llamadas (Call History)

## 📋 Descripción

Sistema reutilizable de auditoría que registra automáticamente el historial de llamadas a endpoints/métodos mediante **AOP (Aspect-Oriented Programming)** y una anotación `@CallHistory`.

## 🎯 Características Principales

- ✅ **Registro automático** mediante anotación
- ✅ **Persistencia asíncrona** (no impacta el tiempo de respuesta)
- ✅ **Tolerancia a fallos** (si falla el registro, no rompe el flujo)
- ✅ **Captura completa**: request, response, errores, metadatos
- ✅ **Masking de campos sensibles**
- ✅ **Truncado automático** de payloads grandes
- ✅ **Índices optimizados** para búsquedas
- ✅ **Arquitectura hexagonal** compatible

## 🏗️ Arquitectura

```
┌─────────────────────┐
│  @CallHistory       │ ← Anotación en método
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  CallHistoryAspect  │ ← Intercepta la llamada
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────┐
│ CallHistoryAsyncWriter  │ ← Persistencia asíncrona
└──────────┬──────────────┘
           │
           ▼
┌──────────────────────────┐
│ CallHistoryRepository    │ ← Guarda en DB
└──────────────────────────┘
```

## 📦 Componentes Creados

### 1. Anotación
- `@CallHistory` - Marca métodos para auditar

### 2. Dominio
- `CallHistoryRecord` - Modelo de dominio

### 3. Puerto de Salida (Out Port)
- `CallHistoryRepositoryPort` - Interface para persistencia

### 4. Infraestructura
- `CallHistoryEntity` - Entidad JPA
- `CallHistoryJpaRepository` - Repositorio Spring Data
- `CallHistoryPersistenceAdapter` - Adaptador de persistencia
- `CallHistoryAspect` - Interceptor AOP
- `CallHistoryAsyncWriter` - Escritor asíncrono
- `AsyncConfig` - Configuración de @Async y AOP

### 5. Base de Datos
- `V4__create_call_history_table.sql` - Migración Flyway

## 🚀 Uso Básico

### Opción 1: Logging Completo

```java
@CallHistory(
    action = "CREATE_EXAMPLE",
    logRequest = true,
    logResponse = true
)
@PostMapping
public ResponseEntity<ExampleResponse> create(@RequestBody CreateExampleRequest request) {
    // Tu lógica aquí
}
```

### Opción 2: Solo Request

```java
@CallHistory(
    action = "LIST_EXAMPLES",
    logRequest = true,
    logResponse = false  // No loguear respuesta (puede ser grande)
)
@GetMapping
public ResponseEntity<List<ExampleResponse>> listAll() {
    // Tu lógica aquí
}
```

### Opción 3: Con Masking de Campos Sensibles

```java
@CallHistory(
    action = "USER_LOGIN",
    logRequest = true,
    logResponse = true,
    maskFields = {"password", "token", "secret"}  // Campos a enmascarar
)
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // Tu lógica aquí
}
```

### Opción 4: Sin Logging de Payload (Solo Metadatos)

```java
@CallHistory(
    action = "EXPORT_LARGE_FILE",
    logRequest = false,   // No guardar request
    logResponse = false   // No guardar response
)
@GetMapping("/export")
public ResponseEntity<byte[]> export() {
    // Tu lógica aquí
}
```

## ⚙️ Parámetros de la Anotación

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `action` | String | "" | Nombre lógico de la acción. Si está vacío, usa el nombre del método |
| `logRequest` | boolean | true | Indica si se debe registrar el request |
| `logResponse` | boolean | false | Indica si se debe registrar la response |
| `maskFields` | String[] | `{"password", "token", "cvv", "pin", "secret"}` | Campos a enmascarar en JSON |
| `maxPayloadSize` | int | 10240 | Tamaño máximo del payload en caracteres (10KB) |

## 📊 Datos Capturados

El sistema captura automáticamente:

### Información HTTP
- Método HTTP (GET, POST, etc.)
- Path del endpoint
- Query parameters
- Cliente IP (considerando proxies)
- User-Agent
- HTTP Status Code

### Información de la Llamada
- Handler (Clase#método)
- Acción lógica
- Request body (si está habilitado)
- Response body (si está habilitado)
- Duración en milisegundos
- Éxito/Fallo

### Metadatos de Trazabilidad
- Correlation ID (desde header `X-Correlation-ID` o generado)
- Trace ID (desde header `X-Trace-ID` o MDC)
- User ID (si está disponible en security context)
- Timestamp

### Información de Errores
- Tipo de excepción
- Mensaje de error
- Stack trace (truncado a 4KB)

## 🔍 Consultar el Historial

### Mediante JPA Repository

```java
@Autowired
private CallHistoryRepositoryPort callHistoryRepository;

// Buscar por correlation ID
List<CallHistoryRecord> records = 
    callHistoryRepository.findByCorrelationId("abc-123");

// Buscar por path
List<CallHistoryRecord> records = 
    callHistoryRepository.findByPath("/api/v1/examples");

// Buscar llamadas fallidas
List<CallHistoryRecord> failures = 
    callHistoryRepository.findBySuccess(false);

// Buscar por rango de fechas
LocalDateTime from = LocalDateTime.now().minusHours(24);
LocalDateTime to = LocalDateTime.now();
List<CallHistoryRecord> records = 
    callHistoryRepository.findByDateRange(from, to);
```

### Mediante SQL Directo

```sql
-- Llamadas de las últimas 24 horas
SELECT * FROM app.call_history 
WHERE created_at >= NOW() - INTERVAL '24 hours'
ORDER BY created_at DESC;

-- Llamadas fallidas
SELECT * FROM app.call_history 
WHERE success = false
ORDER BY created_at DESC;

-- Llamadas por endpoint
SELECT path, COUNT(*), AVG(duration_ms), MAX(duration_ms)
FROM app.call_history
WHERE created_at >= NOW() - INTERVAL '7 days'
GROUP BY path
ORDER BY COUNT(*) DESC;

-- Llamadas lentas (> 1 segundo)
SELECT * FROM app.call_history 
WHERE duration_ms > 1000
ORDER BY duration_ms DESC;

-- Buscar por correlation ID (rastrear transacciones)
SELECT * FROM app.call_history 
WHERE correlation_id = 'abc-123'
ORDER BY created_at;
```

## 🛡️ Seguridad y Privacidad

### Masking Automático

El sistema enmascara automáticamente campos sensibles definidos en `maskFields`:

```json
// Request original
{
  "username": "john",
  "password": "secret123",
  "token": "abc-xyz"
}

// Guardado en DB
{
  "username": "john",
  "password": "***MASKED***",
  "token": "***MASKED***"
}
```

### Truncado de Payloads Grandes

- Por defecto: 10KB máximo
- Configurable mediante `maxPayloadSize`
- Payloads mayores se truncan con indicador `[TRUNCATED]`

## 📈 Performance

### Persistencia Asíncrona

- El registro se hace en **thread pool separado**
- **No impacta** el tiempo de respuesta del endpoint
- Pool configurado en `AsyncConfig`:
  - Core pool size: 2
  - Max pool size: 5
  - Queue capacity: 100

### Tolerancia a Fallos

- Si falla el guardado: **solo se loguea el error**
- El flujo principal **nunca falla** por el historial

## 🔧 Configuración

### ThreadPool (Opcional)

Modificar en `AsyncConfig.java`:

```java
executor.setCorePoolSize(2);     // Threads mínimos
executor.setMaxPoolSize(5);      // Threads máximos
executor.setQueueCapacity(100);  // Cola de espera
```

## 📁 Estructura de la Tabla

```sql
CREATE TABLE app.call_history (
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
```

## 🎨 Casos de Uso

### 1. Debugging de Producción
```java
@CallHistory(action = "PAYMENT_PROCESS", logRequest = true, logResponse = true)
```
Permite ver exactamente qué entró y salió en caso de errores.

### 2. Auditoría de Operaciones Críticas
```java
@CallHistory(action = "DELETE_USER", logRequest = true)
```
Registro de quién borró qué y cuándo.

### 3. Análisis de Performance
Consultar `duration_ms` para identificar endpoints lentos.

### 4. Rastreo de Transacciones Distribuidas
Usar `correlation_id` para seguir una transacción completa.

## 🚀 Cómo Replicar en Otro Proyecto

1. **Copiar el paquete completo** `infrastructure.history`
2. **Copiar la anotación** `@CallHistory`
3. **Copiar el modelo de dominio** `CallHistoryRecord`
4. **Copiar el puerto** `CallHistoryRepositoryPort`
5. **Copiar adaptadores de persistencia**
6. **Agregar migración Flyway** `V4__create_call_history_table.sql`
7. **Agregar configuración** `AsyncConfig`
8. **Agregar dependencia** en `build.gradle`:
   ```gradle
   implementation 'org.springframework.boot:spring-boot-starter-aop'
   ```
9. **Usar la anotación** en los métodos que necesites auditar

## 📝 Notas Importantes

- ⚠️ No abusar del `logResponse=true` en endpoints que retornan datos grandes
- ⚠️ Revisar periódicamente el tamaño de la tabla y configurar limpieza automática si es necesario
- ✅ Los índices están optimizados para búsquedas comunes
- ✅ Compatible con arquitectura hexagonal/limpia
- ✅ Totalmente desacoplado del negocio

## 🔮 Mejoras Futuras Posibles

- Endpoint REST para consultar el historial
- Panel de administración/visualización
- Exportación a Kafka/ELK para análisis
- Limpieza automática de registros antiguos
- Métricas y dashboards (Prometheus/Grafana)
- Compresión de payloads grandes
