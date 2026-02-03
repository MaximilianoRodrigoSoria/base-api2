# 🚀 Base API 2

API REST base con Spring Boot 4.0.2 (Java 17), arquitectura hexagonal, Redis cache y Docker optimizado.

## ✨ Características

- ✅ **Arquitectura Hexagonal** (Ports & Adapters)
- ✅ **Spring Boot 4.0.2** con Java 17
- ✅ **PostgreSQL 15** como base de datos principal
- ✅ **Redis 7** para caché distribuido
- ✅ **H2** para tests (base de datos en memoria)
- ✅ **Docker Multi-stage Build** (imagen optimizada ~200MB)
- ✅ **Flyway** para migraciones de BD
- ✅ **OpenAPI/Swagger** para documentación
- ✅ **Spotless** con formato automático de código
- ✅ **Tests unitarios** completos con JUnit 5 + Mockito

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
    └── config/             # Configuración (Cache, Swagger, etc)
```

## 🚀 Tecnologías

### Backend
- **Java 17**
- **Spring Boot 4.0.2**
  - Spring Data JPA
  - Spring Data Redis
  - Spring Cache
  - Spring Validation
- **Lombok**

### Base de Datos
- **PostgreSQL 15** (producción)
- **H2** (tests)
- **Flyway** (migraciones)

### Caché
- **Redis 7** (Alpine)

### Calidad de Código
- **Spotless** (formato automático)
- **JUnit 5 + Mockito** (testing)

### DevOps
- **Docker** con multi-stage build
- **Docker Compose** (orquestación)

## 📋 Requisitos

- Java 17+
- Docker y Docker Compose
- Gradle 8.14+

## 🚀 Inicio Rápido

### Opción 1: Con Docker (Recomendado)

```bash
# Levantar todos los servicios (PostgreSQL, Redis y App)
docker-compose up --build

# En modo detached (segundo plano)
docker-compose up --build -d
```

La aplicación estará disponible en:
- **API**: http://localhost:8080/base-api2
- **Swagger UI**: http://localhost:8080/base-api2/swagger-ui.html
- **API Docs**: http://localhost:8080/base-api2/api-docs

### Opción 2: Desarrollo Local

```bash
# 1. Levantar infraestructura (PostgreSQL y Redis)
docker-compose up postgres redis -d

# 2. Compilar
./gradlew clean build

# 3. Ejecutar
./gradlew bootRun
```

## 🐳 Docker

El proyecto incluye un **Dockerfile multi-stage optimizado**:

- **Fase 1**: Compilación con Gradle 8.14 + JDK 17 Alpine
- **Fase 2**: Runtime con JRE 17 Alpine

**Características:**
- 🏷️ Etiquetas informativas durante el build
- 📦 Imagen final ~200MB (70% más liviana)
- 🔒 Usuario no-root para seguridad
- ✨ Spotless se ejecuta automáticamente

Ver documentación completa: [DOCKER.md](DOCKER.md)

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring Boot | `local` |
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL | `jdbc:postgresql://localhost:5432/baseapi2` |
| `SPRING_DATA_REDIS_HOST` | Host de Redis | `localhost` |
| `SPRING_DATA_REDIS_PORT` | Puerto de Redis | `6379` |

### Profiles

- **local**: Desarrollo local (PostgreSQL + Redis)
- **test**: Tests (H2 + Simple Cache)

## 🔌 Endpoints

### Base URL
```
http://localhost:8080/base-api2/api/v1
```

### 1. Crear Example

```http
POST /examples
Content-Type: application/json

{
  "name": "John Doe",
  "dni": "12345678"
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "dni": "12345678"
}
```

### 2. Listar Examples

```http
GET /examples
```

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "dni": "12345678"
  }
]
```

### 3. Buscar por DNI (⚡ Con Caché Redis)

```http
GET /examples/dni/{dni}
```

**Ejemplo:**
```bash
curl http://localhost:8080/base-api2/api/v1/examples/dni/12345678
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "dni": "12345678"
}
```

**Características:**
- ⚡ Primera consulta: busca en BD y cachea en Redis
- 🚀 Siguientes consultas: obtiene del caché (más rápido)
- 🔄 Al crear: invalida caché automáticamente
- ⏱️ TTL: 10 minutos

## 🗄️ Redis Cache

El proyecto utiliza **Redis** para mejorar el rendimiento:

```java
@Cacheable(value = "examplesByDni", key = "#dni")
public ExampleResponse findByDni(String dni) {
    // Primera llamada: consulta BD y cachea
    // Siguientes llamadas: obtiene del caché
}

@CacheEvict(value = "examplesByDni", key = "#request.dni")
public ExampleResponse create(CreateExampleRequest request) {
    // Invalida caché al crear
}
```

### Comandos Útiles

```bash
# Ver todas las claves en Redis
docker exec -it baseapi2-redis redis-cli KEYS "*"

# Limpiar caché
docker exec -it baseapi2-redis redis-cli FLUSHALL

# Monitorear en tiempo real
docker exec -it baseapi2-redis redis-cli MONITOR
```

Ver documentación completa: [REDIS.md](REDIS.md)

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Con reporte de coverage
./gradlew test jacocoTestReport

# Tests específicos
./gradlew test --tests FindExampleByDniServiceTest
```

### Configuración de Tests

Los tests usan **H2** (en memoria) en lugar de PostgreSQL:

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
  cache:
    type: simple  # Cache simple en lugar de Redis
```

## 📚 Documentación API (Swagger)

La API está documentada con **OpenAPI 3.0**:

- **Swagger UI**: http://localhost:8080/base-api2/swagger-ui.html
- **JSON Spec**: http://localhost:8080/base-api2/api-docs

## ✨ Spotless - Formato Automático

El código se formatea automáticamente antes de cada compilación:

```bash
# Aplicar formato manualmente
./gradlew spotlessApply

# Verificar formato
./gradlew spotlessCheck
```

**Configuración:**
- Google Java Format (AOSP)
- Indentación: 4 espacios
- Imports ordenados y sin duplicados

## 🐛 Manejo de Errores

Todas las respuestas de error siguen el mismo formato:

```json
{
  "timestamp": "2026-02-02T19:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Example no encontrado con DNI: 12345678",
  "path": "/api/v1/examples/dni/12345678",
  "traceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Códigos de Error

| Código | Descripción |
|--------|-------------|
| 400 | Bad Request - Validación fallida |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - DNI duplicado |
| 500 | Internal Server Error |

## 📦 Base de Datos

### Migraciones con Flyway

```sql
-- V1__create_schema.sql
CREATE SCHEMA IF NOT EXISTS app;

-- V2__create_example_table.sql
CREATE TABLE app.examples (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V3__insert_example_seed_data.sql
INSERT INTO app.examples (name, dni)
VALUES ('John Doe', '12345678');
```

## 🛠️ Comandos Útiles

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de la app
docker-compose logs -f app

# Reiniciar servicios
docker-compose restart

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir sin caché
docker-compose build --no-cache

# Ver estado de servicios
docker-compose ps
```

## 📊 Arquitectura de Servicios

```
┌─────────────────┐
│   Docker Host   │
├─────────────────┤
│                 │
│  ┌───────────┐  │
│  │    App    │  │ :8080
│  │ Spring    │  │
│  │ Boot      │  │
│  └─────┬─────┘  │
│        │        │
│  ┌─────┴─────┐  │
│  │           │  │
│  ▼           ▼  │
│ ┌──────┐  ┌────┐│
│ │ Post │  │Redis││ :5432, :6379
│ │ greSQL  │    ││
│ └──────┘  └────┘│
│                 │
└─────────────────┘
```

## 🎯 Roadmap

- [x] Arquitectura hexagonal
- [x] Docker multi-stage build
- [x] Redis cache
- [x] H2 para tests
- [x] OpenAPI/Swagger
- [x] Spotless automático
- [x] Endpoint búsqueda por DNI
- [ ] Paginación en listados
- [ ] Actualización y eliminación
- [ ] MapStruct para mapeos
- [ ] Spring Security
- [ ] Actuator y métricas
- [ ] CI/CD con GitHub Actions

## 📖 Documentación Adicional

- [DOCKER.md](DOCKER.md) - Guía completa de Docker
- [REDIS.md](REDIS.md) - Guía de Redis y caché

## 🤝 Contribuir

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Aplicar formato (`./gradlew spotlessApply`)
4. Ejecutar tests (`./gradlew test`)
5. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
6. Push a la rama (`git push origin feature/nueva-funcionalidad`)
7. Crear Pull Request

## 📝 Licencia

Este proyecto es un template base para desarrollo de APIs REST con Spring Boot.

---

⭐ **¿Te fue útil?** Dale una estrella al repositorio
