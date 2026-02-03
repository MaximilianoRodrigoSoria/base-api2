# 📦 Migraciones de Base de Datos con Flyway

## ¿Qué es Flyway?

Flyway es una herramienta de migración de base de datos que permite versionar y aplicar cambios de esquema de forma controlada y reproducible.

## 📋 Características

- ✅ **Versionamiento automático** de cambios en la base de datos
- ✅ **Migraciones SQL puras** - Sin abstracción compleja
- ✅ **Ejecución ordenada** - Por número de versión
- ✅ **Histórico completo** - Tabla `flyway_schema_history`
- ✅ **Idempotencia** - Las migraciones ejecutadas no se vuelven a aplicar

## 📁 Estructura de Migraciones

Las migraciones se almacenan en:
```
src/main/resources/db/migration/
├── V1__create_schema.sql
├── V2__create_example_table.sql
├── V3__insert_example_seed_data.sql
└── V4__create_call_history_table.sql
```

### Convención de Nombres

```
V{VERSION}__{DESCRIPTION}.sql

Ejemplos:
V1__create_schema.sql
V2__create_example_table.sql
V3__insert_seed_data.sql
```

**Reglas:**
- Prefijo `V` obligatorio (V mayúscula)
- Número de versión (1, 2, 3... o 1.0, 1.1, 2.0...)
- Doble guion bajo `__` como separador
- Descripción en snake_case o palabras separadas por guiones
- Extensión `.sql`

## 📝 Migraciones Actuales

### V1: Crear Schema

```sql
-- V1__create_schema.sql
CREATE SCHEMA IF NOT EXISTS app;

COMMENT ON SCHEMA app IS 'Schema principal de la aplicación';
```

**Propósito:** Crear el esquema `app` donde vivirán todas las tablas.

---

### V2: Crear Tabla Examples

```sql
-- V2__create_example_table.sql
CREATE TABLE app.examples (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_examples_dni ON app.examples(dni);

COMMENT ON TABLE app.examples IS 'Tabla de ejemplos para demostración';
COMMENT ON COLUMN app.examples.dni IS 'Documento Nacional de Identidad';
```

**Propósito:** Crear la tabla principal de examples con índice en DNI.

---

### V3: Insertar Datos Semilla

```sql
-- V3__insert_example_seed_data.sql
INSERT INTO app.examples (name, dni)
VALUES 
    ('John Doe', '12345678'),
    ('Jane Smith', '87654321'),
    ('Bob Johnson', '11223344')
ON CONFLICT (dni) DO NOTHING;
```

**Propósito:** Insertar datos iniciales para desarrollo/testing.

---

### V4: Crear Tabla Call History

```sql
-- V4__create_call_history_table.sql
CREATE TABLE app.call_history (
    id BIGSERIAL PRIMARY KEY,
    correlation_id VARCHAR(100) NOT NULL,
    method VARCHAR(10) NOT NULL,
    path VARCHAR(500) NOT NULL,
    status_code INTEGER NOT NULL,
    success BOOLEAN NOT NULL,
    duration_ms BIGINT NOT NULL,
    request_body TEXT,
    response_body TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_call_history_correlation_id ON app.call_history(correlation_id);
CREATE INDEX idx_call_history_path ON app.call_history(path);
CREATE INDEX idx_call_history_created_at ON app.call_history(created_at);
CREATE INDEX idx_call_history_success ON app.call_history(success);

COMMENT ON TABLE app.call_history IS 'Historial de llamadas HTTP para auditoría';
```

**Propósito:** Crear tabla para almacenar el historial de todas las llamadas HTTP.

## 🚀 Ejecución

### Automática

Flyway se ejecuta automáticamente al iniciar la aplicación:

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    schemas: app
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Manual con Gradle

```bash
# Ejecutar migraciones
./gradlew flywayMigrate

# Ver información del estado
./gradlew flywayInfo

# Validar migraciones
./gradlew flywayValidate

# Limpiar base de datos (¡CUIDADO! Borra todo)
./gradlew flywayClean
```

## 📊 Ver Historial de Migraciones

Conectarse a PostgreSQL y consultar la tabla de Flyway:

```bash
# Conectar a la base de datos
docker exec -it baseapi2-postgres psql -U postgres -d baseapi2

# Ver historial de migraciones
SELECT version, description, type, script, installed_on, execution_time, success 
FROM app.flyway_schema_history 
ORDER BY installed_rank;
```

**Ejemplo de salida:**
```
 version |        description         |  type  |           script            |     installed_on        | execution_time | success 
---------+----------------------------+--------+-----------------------------+------------------------+----------------+---------
 1       | create schema              | SQL    | V1__create_schema.sql       | 2026-02-03 16:00:00    | 45             | t
 2       | create example table       | SQL    | V2__create_example_table... | 2026-02-03 16:00:01    | 120            | t
 3       | insert example seed data   | SQL    | V3__insert_example_seed_... | 2026-02-03 16:00:02    | 30             | t
 4       | create call history table  | SQL    | V4__create_call_history_... | 2026-02-03 16:00:03    | 150            | t
```

## ➕ Crear Nueva Migración

### Paso 1: Crear archivo

```bash
# Crear nuevo archivo con la siguiente versión
touch src/main/resources/db/migration/V5__add_email_to_examples.sql
```

### Paso 2: Escribir SQL

```sql
-- V5__add_email_to_examples.sql
ALTER TABLE app.examples 
ADD COLUMN email VARCHAR(255);

CREATE INDEX idx_examples_email ON app.examples(email);

COMMENT ON COLUMN app.examples.email IS 'Email del ejemplo';
```

### Paso 3: Aplicar

```bash
# Reiniciar la aplicación o ejecutar
./gradlew flywayMigrate
```

## ⚠️ Buenas Prácticas

### ✅ DO

- ✅ **Nunca modificar** migraciones ya aplicadas
- ✅ **Probar migraciones** en ambiente local primero
- ✅ **Usar transacciones** cuando sea posible
- ✅ **Nombres descriptivos** para las migraciones
- ✅ **Incluir rollback** manual si es necesario (en comentarios)
- ✅ **Versionar incrementalmente** (V1, V2, V3...)

### ❌ DON'T

- ❌ **NO cambiar** scripts ya ejecutados en producción
- ❌ **NO saltar versiones** (V1 → V3, salteando V2)
- ❌ **NO usar DDL y DML** mezclados sin cuidado
- ❌ **NO operaciones largas** sin considerar downtime
- ❌ **NO olvidar índices** en columnas consultadas

## 🔄 Rollback

Flyway **NO soporta rollback automático**. Si necesitas revertir:

### Opción 1: Nueva Migración Correctiva

```sql
-- V6__remove_email_from_examples.sql
ALTER TABLE app.examples DROP COLUMN email;
```

### Opción 2: Script Manual

Crear script de rollback comentado en la migración original:

```sql
-- V5__add_email_to_examples.sql
ALTER TABLE app.examples ADD COLUMN email VARCHAR(255);

/*
-- ROLLBACK:
-- ALTER TABLE app.examples DROP COLUMN email;
*/
```

## 🐳 Flyway en Docker

Al usar Docker Compose, Flyway se ejecuta automáticamente:

```yaml
# docker-compose.yml
services:
  app:
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_FLYWAY_ENABLED: "true"
```

**Orden de ejecución:**
1. PostgreSQL se levanta y está "healthy"
2. La aplicación inicia
3. Flyway verifica migraciones pendientes
4. Aplica migraciones en orden
5. La aplicación queda lista

## 🧪 Flyway en Tests

En tests se usa H2, pero Flyway también se ejecuta:

```yaml
# application-test.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
```

**Ventaja:** Los tests tienen el mismo esquema que producción.

## 🔍 Troubleshooting

### Error: "Validate failed: Migrations have failed validation"

**Causa:** Se modificó una migración ya aplicada.

**Solución:**
```bash
# Opción 1: Reparar el checksum
./gradlew flywayRepair

# Opción 2: Limpiar y re-migrar (solo desarrollo)
./gradlew flywayClean flywayMigrate
```

### Error: "Found non-empty schema(s) without schema history table"

**Causa:** Base de datos tiene tablas pero no tiene historial de Flyway.

**Solución:**
```yaml
spring:
  flyway:
    baseline-on-migrate: true  # Crear baseline automáticamente
```

### Error: Migration version out of order

**Causa:** Se intentó aplicar V3 después de que ya existe V4.

**Solución:**
```yaml
spring:
  flyway:
    out-of-order: true  # Permitir migraciones fuera de orden (no recomendado)
```

## 📚 Referencias

- [Documentación oficial de Flyway](https://flywaydb.org/documentation/)
- [Flyway con Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [Mejores prácticas](https://flywaydb.org/documentation/concepts/migrations#best-practices)

---

**Volver al [README principal](../README.md)**
