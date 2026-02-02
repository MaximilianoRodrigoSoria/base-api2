# Base API 2

API REST base con Spring Boot 4.0.2 (Java 17) y arquitectura hexagonal.

## 🏗️ Arquitectura

El proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)**:

```
src/main/java/com/ar/laboratory/baseapi2/
├── application/              # Capa de aplicación
│   ├── dto/                 # DTOs (Request/Response)
│   ├── port/
│   │   ├── in/             # Puertos de entrada (Use Cases)
│   │   └── out/            # Puertos de salida (Repositorios)
│   └── usecase/            # Implementación de casos de uso
├── domain/                  # Capa de dominio
│   ├── model/              # Modelos de dominio
│   └── exception/          # Excepciones de negocio
└── infrastructure/          # Capa de infraestructura
    ├── adapter/
    │   ├── in/web/         # Controllers REST
    │   └── out/persistence/ # Implementación JPA
    └── config/             # Configuración (Exception Handler, etc)
```

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (migraciones)
- **Lombok**
- **Validation API**
- **JUnit 5 + Mockito** (testing)
- **Testcontainers** (tests de integración)

## 📋 Requisitos

- Java 17+
- Docker y Docker Compose
- Gradle 8.x

## 🔧 Configuración

### 1. Levantar PostgreSQL con Docker

```bash
docker-compose up -d
```

Esto levantará PostgreSQL en:
- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: baseapi2
- **Usuario**: postgres
- **Contraseña**: postgres

### 2. Compilar el proyecto

```bash
gradlew clean build
```

### 3. Ejecutar tests

```bash
gradlew test
```

### 4. Ejecutar la aplicación

```bash
gradlew bootRun
```

La API estará disponible en: `http://localhost:8080`

## 🔌 Endpoints

### Crear Example

```http
POST /examples
Content-Type: application/json

{
  "name": "John Doe",
  "dni": "12345678"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "dni": "12345678"
}
```

**Errores posibles:**
- `400 Bad Request`: Validación fallida
- `409 Conflict`: DNI duplicado

### Listar Examples

```http
GET /examples
```

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "dni": "12345678"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "dni": "87654321"
  }
]
```

## 🧪 Testing

El proyecto incluye:

- **Tests unitarios** de casos de uso (Mockito)
- **Tests de controller** (`@WebMvcTest`)
- **Tests de integración** (Testcontainers - opcional)

Ejecutar tests:
```bash
gradlew test
```

Ver reporte de coverage:
```bash
gradlew test jacocoTestReport
```

## 🐛 Manejo de Errores

Respuesta estándar de error:

```json
{
  "timestamp": "2026-02-02T19:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un Example con DNI: 12345678",
  "path": "/examples",
  "traceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Para errores de validación:

```json
{
  "timestamp": "2026-02-02T19:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "path": "/examples",
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "validationErrors": {
    "name": "El nombre es obligatorio",
    "dni": "El DNI debe ser alfanumérico"
  }
}
```

## 📦 Base de Datos

La migración inicial de Flyway crea la tabla `examples`:

```sql
CREATE TABLE examples (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE
);
```

## 🎯 Próximos Pasos

- [ ] Agregar paginación al listado
- [ ] Implementar búsqueda por ID
- [ ] Agregar actualización y eliminación
- [ ] Implementar MapStruct para mapeos
- [ ] Agregar documentación con OpenAPI/Swagger
- [ ] Implementar seguridad con Spring Security
- [ ] Agregar métricas con Actuator
- [ ] Configurar CI/CD

## 📝 Licencia

Este proyecto es un template base para desarrollo de APIs REST.
