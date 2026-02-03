<p align="center">
<a href="https://www.linkedin.com/in/soriamaximilianorodrigo/" target="_blank" rel="noopener noreferrer">
<img width="100%" height="100%" src="docs/img/banner.gif" alt="Linkedin"></a></p>


<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Spring_Boot-3.2.2-brightgreen" alt="Spring Boot"></a>
  <a href="#"><img src="https://img.shields.io/badge/chat-on%20Discord-7289da.svg?sanitize=true" alt="Chat"></a>
  <a href="#"><img src="https://img.shields.io/badge/Java-21-orange" alt="Java"></a>
  <a href="docs/postman/Base API 2 - REST API.postman_collection.json" download><img src="https://img.shields.io/badge/Postman-Collection-orange?logo=postman&logoColor=white" alt="Postman Collection"></a>
</p>

<br>
<br>
<p align="center">


# 🚀 Base API 2

API REST base con Spring Boot 4.0.2 (Java 21), arquitectura hexagonal, Redis cache, logging avanzado y herramientas de calidad de código.

## ✨ Características

### Arquitectura y Desarrollo
- ✅ **Arquitectura Hexagonal** (Ports & Adapters)
- ✅ **Spring Boot 4.0.2** con Java 21
- ✅ **Lombok** para reducir boilerplate

### Base de Datos y Persistencia
- ✅ **PostgreSQL 15** como base de datos principal
- ✅ **Redis 7** para caché distribuido con TTL configurable
- ✅ **H2** para tests (base de datos en memoria)
- ✅ **Flyway** para migraciones versionadas

### Seguridad y Logging
- ✅ **Sistema de Logging Avanzado** con sanitización automática
  - Filtro HTTP que captura requests/responses
  - Enmascaramiento de datos sensibles (passwords, tokens, DNIs, CUIT, tarjetas)
  - Truncamiento de bodies grandes
  - Exclusión de endpoints de monitoreo
- ✅ **LogSanitizer** para protección de información sensible en logs

### Calidad de Código
- ✅ **Spotless** con formato automático (Google Java Format AOSP)
- ✅ **Checkstyle** para validación de convenciones de nombrado
- ✅ **PMD** para detección de code smells
- ✅ **SpotBugs** para análisis estático de bugs
- ✅ **4 herramientas de calidad** integradas en el build

### Testing
- ✅ **JUnit 5 + Mockito** para tests unitarios
- ✅ **Testcontainers** para tests de integración con PostgreSQL real
- ✅ **WireMock** para mocking de APIs HTTP
- ✅ **WebTestClient** para testing de APIs REST

### DevOps
- ✅ **Docker Multi-stage Build** (imagen optimizada ~200MB)
- ✅ **Docker Compose** con healthchecks
- ✅ **OpenAPI/Swagger** para documentación interactiva

## 🏗️ Arquitectura

El proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)**:

```
src/main/java/com/ar/laboratory/baseapi2/
│
├── 📦 example/                              # Módulo de dominio: Example
│   ├── application/                         # ⚙️ Capa de Aplicación
│   │   ├── inbound/                        # Puertos de entrada
│   │   │   └── command/                    # Comandos (DTOs de entrada)
│   │   │       ├── CreateExampleCommand.java
│   │   │       ├── FindExampleByDniCommand.java
│   │   │       └── ListExamplesCommand.java
│   │   ├── outbound/                       # Puertos de salida
│   │   │   └── port/
│   │   │       └── ExampleRepositoryPort.java  # Interface del repositorio
│   │   └── usecase/                        # Casos de uso (lógica de aplicación)
│   │       ├── CreateExampleUseCase.java
│   │       ├── FindExampleByDniUseCase.java
│   │       └── ListExamplesUseCase.java
│   │
│   ├── domain/                              # 🎯 Capa de Dominio
│   │   ├── model/
│   │   │   └── Example.java               # Entidad de dominio
│   │   └── exception/                      # Excepciones de negocio
│   │       ├── ExampleAlreadyExistsException.java
│   │       └── ExampleNotFoundException.java
│   │
│   └── infrastructure/                      # 🔌 Capa de Infraestructura
│       ├── config/
│       │   └── ExampleConfig.java          # Configuración de beans
│       ├── inbound/                        # Adaptadores de entrada
│       │   └── web/
│       │       ├── api/
│       │       │   └── ExampleApi.java     # OpenAPI interface
│       │       ├── controller/
│       │       │   └── ExampleController.java  # REST Controller
│       │       ├── dto/                    # DTOs de entrada/salida
│       │       │   ├── CreateExampleRequest.java
│       │       │   └── ExampleResponse.java
│       │       └── mapper/
│       │           └── ExampleDtoMapper.java   # Mapeo DTO ↔ Domain
│       └── outbound/                       # Adaptadores de salida
│           └── persistence/
│               ├── adapter/
│               │   └── ExamplePersistenceAdapter.java  # Impl del puerto
│               ├── entity/
│               │   └── ExampleEntity.java  # Entidad JPA
│               ├── mapper/
│               │   └── ExampleEntityMapper.java  # Mapeo Domain ↔ Entity
│               └── repository/
│                   └── ExampleJpaRepository.java  # Spring Data JPA
│
├── 📦 callhistory/                          # Módulo de dominio: Call History
│   ├── application/                         # ⚙️ Capa de Aplicación
│   │   ├── inbound/
│   │   │   └── command/                    # 6 comandos de consulta
│   │   │       ├── FindByCorrelationIdCommand.java
│   │   │       ├── FindByDateRangeCommand.java
│   │   │       ├── FindByIdCommand.java
│   │   │       ├── FindByPathCommand.java
│   │   │       ├── FindBySuccessCommand.java
│   │   │       └── ListCallHistoryCommand.java
│   │   ├── outbound/
│   │   │   └── port/
│   │   │       └── CallHistoryRepositoryPort.java
│   │   └── usecase/                        # 6 casos de uso
│   │       ├── FindByCorrelationIdUseCase.java
│   │       ├── FindByDateRangeUseCase.java
│   │       ├── FindByIdUseCase.java
│   │       ├── FindByPathUseCase.java
│   │       ├── FindBySuccessUseCase.java
│   │       └── ListCallHistoryUseCase.java
│   │
│   ├── domain/                              # 🎯 Capa de Dominio
│   │   ├── model/
│   │   │   └── CallHistoryRecord.java
│   │   └── exception/
│   │       └── CallHistoryNotFoundException.java
│   │
│   └── infrastructure/                      # 🔌 Capa de Infraestructura
│       ├── config/
│       │   └── CallHistoryConfig.java
│       ├── inbound/
│       │   └── web/
│       │       ├── api/
│       │       │   └── CallHistoryApi.java
│       │       ├── controller/
│       │       │   └── CallHistoryController.java
│       │       ├── dto/
│       │       │   └── CallHistoryResponse.java
│       │       └── mapper/
│       │           └── CallHistoryDtoMapper.java
│       └── outbound/
│           └── persistence/
│               ├── adapter/
│               │   └── CallHistoryPersistenceAdapter.java
│               ├── entity/
│               │   └── CallHistoryEntity.java
│               ├── mapper/
│               │   └── CallHistoryEntityMapper.java
│               └── repository/
│                   └── CallHistoryJpaRepository.java
│
└── 📦 shared/                               # 🔧 Componentes Compartidos
    └── infrastructure/
        ├── annotation/
        │   └── CallHistory.java            # Anotación para auditoría
        ├── cache/
        │   └── CacheLoggingAspect.java     # Aspecto para logging de caché
        ├── config/
        │   ├── AsyncConfig.java            # Configuración de async
        │   ├── CacheConfig.java            # Configuración de Redis
        │   ├── ErrorResponse.java          # DTO de respuesta de error
        │   ├── GlobalExceptionHandler.java # Manejo global de excepciones
        │   └── OpenApiConfig.java          # Configuración de Swagger
        ├── exception/
        │   ├── BadRequestException.java
        │   └── InfrastructureException.java
        ├── history/
        │   ├── CallHistoryAspect.java      # Aspecto para auditoría de llamadas
        │   └── CallHistoryAsyncWriter.java # Escritura asíncrona de historial
        ├── logging/
        │   ├── LoggingFilter.java          # Filtro HTTP de logging
        │   └── LogSanitizer.java           # Sanitización de datos sensibles
        └── web/
            └── api/
                └── StandardApiResponses.java  # Respuestas API estándar
```

### 📐 Principios de la Arquitectura Hexagonal

```
┌─────────────────────────────────────────────────────────────┐
│                    🌐 ADAPTADORES DE ENTRADA                 │
│              (Controllers, REST API, Web Layer)              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    ⚙️ CAPA DE APLICACIÓN                     │
│          (Use Cases, Commands, Application Services)        │
│                                                              │
│  ┌────────────────┐              ┌─────────────────┐        │
│  │ Puertos de     │              │ Puertos de      │        │
│  │ Entrada (IN)   │              │ Salida (OUT)    │        │
│  │ • Commands     │              │ • Repository    │        │
│  │ • Use Cases    │              │   Interfaces    │        │
│  └────────────────┘              └─────────────────┘        │
└────────────────────────┬────────────────┬───────────────────┘
                         │                │
                         ▼                │
┌─────────────────────────────────────────┘
│                    🎯 CAPA DE DOMINIO
│            (Entities, Value Objects, Domain Logic)
│                  (Sin dependencias externas)
└─────────────────────────────────────────┐
                                          │
                                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   🔌 ADAPTADORES DE SALIDA                   │
│         (JPA, PostgreSQL, Redis, External APIs)              │
└─────────────────────────────────────────────────────────────┘
```

### 🔄 Flujo de una Petición HTTP

```
1. HTTP Request
   ↓
2. Controller (Adaptador de Entrada)
   ↓
3. Mapper: DTO → Command
   ↓
4. Use Case (Lógica de Aplicación)
   ↓
5. Domain Model (Lógica de Negocio)
   ↓
6. Repository Port (Interface)
   ↓
7. Persistence Adapter (Adaptador de Salida)
   ↓
8. JPA Repository → PostgreSQL
   ↓
9. Entity → Domain Model
   ↓
10. Domain Model → DTO
    ↓
11. HTTP Response
```

## 🚀 Tecnologías

### Backend
- **Java 21**
- **Spring Boot 4.0.2**
  - Spring Data JPA
  - Spring Data Redis
  - Spring Cache
  - Spring Validation
  - Spring WebFlux (testing)
- **Lombok**

### Base de Datos
- **PostgreSQL 15** (producción)
- **H2** (tests unitarios)
- **Flyway 10.20.1** (migraciones)

### Caché
- **Redis 7** (Alpine)

### Calidad de Código
- **Spotless 6.25.0** (formato automático - Google Java Format AOSP)
- **Checkstyle 10.20.2** (naming conventions y estructura)
- **PMD 7.8.0** (detección de code smells)
- **SpotBugs 4.8.6** (análisis estático de bugs)

### Testing
- **JUnit 5** + **Mockito** (tests unitarios)
- **Testcontainers 1.20.4** (tests de integración)
- **WireMock 3.9.2** (HTTP mocking)
- **WebTestClient** (testing de APIs)

### DevOps
- **Docker** con multi-stage build
- **Docker Compose** (orquestación)

## 📋 Requisitos

- Java 21+
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

# 2. Compilar (incluye Spotless automáticamente)
./gradlew clean build

# 3. Ejecutar
./gradlew bootRun
```

## 🐳 Docker

El proyecto incluye un **Dockerfile multi-stage optimizado**:

- **Fase 1**: Compilación con Gradle 8.14 + JDK 21 Alpine
- **Fase 2**: Runtime con JRE 21 Alpine

**Características:**
- 🏷️ Etiquetas informativas durante el build
- 📦 Imagen final ~200MB (70% más liviana)
- 🔒 Usuario no-root para seguridad
- ✨ Spotless se ejecuta automáticamente
- 🔍 Todas las herramientas de calidad validadas en build

Ver documentación completa: [DOCKER.md](DOCKER.md)

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring Boot | `local` |
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL | `jdbc:postgresql://localhost:5432/baseapi2` |
| `SPRING_DATA_REDIS_HOST` | Host de Redis | `localhost` |
| `SPRING_DATA_REDIS_PORT` | Puerto de Redis | `6379` |
| `JAVA_OPTS` | Opciones de JVM | `-Xmx512m -Xms256m` |

### Profiles

- **local**: Desarrollo local (PostgreSQL + Redis + Logging completo)
- **test**: Tests (H2 + Simple Cache + Logs mínimos)

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
- ⏱️ TTL: 10 minutos (configurable)

## 🔒 Sistema de Logging y Seguridad

### Características del Sistema de Logging

El proyecto incluye un **sistema de logging avanzado** con sanitización automática de datos sensibles:

#### LoggingFilter
- 🔍 Captura todas las peticiones HTTP (request + response)
- 📊 Mide duración de cada request
- 🚫 Excluye endpoints de actuator, swagger y recursos estáticos
- 📦 Trunca bodies grandes (máx 10KB) para evitar saturar logs
- 🔒 **Sanitiza datos sensibles antes de loguear**

#### LogSanitizer
Enmascara automáticamente:
- 🔑 Passwords, secrets, tokens, API keys
- 🎫 Headers de autorización (Authorization, X-API-Key, etc.)
- 🆔 DNI argentino (8 dígitos)
- 🏢 CUIT/CUIL (11 dígitos)
- 💳 Tarjetas de crédito (13-19 dígitos)

**Ejemplo de logs:**

```log
HTTP POST /api/v1/examples | status=201 | duration=145ms | timestamp=2026-02-03T10:00:00Z
REQUEST: {"name":"John Doe","dni":"****"}
RESPONSE: {"id":1,"name":"John Doe","dni":"****"}
```

### Configurar Paths Excluidos

```java
// LoggingFilter.java
private static final Set<String> SKIP_PATH_PREFIXES =
    Set.of("/actuator", "/swagger-ui", "/v3/api-docs");
```

### Agregar Nuevas Reglas de Sanitización

```java
// LogSanitizer.java - Agregar nueva regla en RULES
new Rule(
    Pattern.compile("(\"email\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
    "$1****$3"
)
```

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

# Ver valor específico
docker exec -it baseapi2-redis redis-cli GET "examplesByDni::12345678"

# Limpiar caché
docker exec -it baseapi2-redis redis-cli FLUSHALL

# Monitorear en tiempo real
docker exec -it baseapi2-redis redis-cli MONITOR

# Ver TTL de una clave
docker exec -it baseapi2-redis redis-cli TTL "examplesByDni::12345678"
```

Ver documentación completa: [REDIS.md](REDIS.md)

## ✨ Calidad de Código

El proyecto integra **4 herramientas de calidad** que se ejecutan automáticamente:

### 1. Spotless - Formato Automático

Formatea el código automáticamente antes de cada compilación:

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
- Se ejecuta automáticamente antes de `compileJava`

### 2. Checkstyle - Convenciones de Código

Valida naming conventions y estructura del código:

```bash
# Ejecutar Checkstyle
./gradlew checkstyleMain checkstyleTest

# Ver reporte HTML
start build/reports/checkstyle/main.html
```

**Valida:**
- ✅ Nombres en PascalCase para clases
- ✅ Nombres en camelCase para métodos y variables
- ✅ Constantes en UPPER_SNAKE_CASE
- ✅ Imports sin wildcards (*)
- ✅ No usar System.out/err (usar logger)
- ✅ Javadoc en clases y métodos públicos
- ✅ Complejidad ciclomática < 15
- ✅ Máximo 7 parámetros por método

### 3. PMD - Detección de Code Smells

Detecta problemas de diseño y malas prácticas:

```bash
# Ejecutar PMD
./gradlew pmdMain pmdTest

# Ver reporte HTML
start build/reports/pmd/main.html
```

**Detecta:**
- 🔍 Código duplicado
- 🔍 Variables no utilizadas
- 🔍 Métodos demasiado largos o complejos
- 🔍 Importaciones innecesarias
- 🔍 Expresiones demasiado complejas

### 4. SpotBugs - Análisis Estático de Bugs

Encuentra bugs potenciales mediante análisis del bytecode:

```bash
# Ejecutar SpotBugs
./gradlew spotbugsMain spotbugsTest

# Ver reporte HTML
start build/reports/spotbugs/main.html
```

**Detecta:**
- 🐛 NullPointerException potenciales
- 🐛 Resource leaks
- 🐛 Thread safety issues
- 🐛 Performance issues
- 🐛 Security vulnerabilities

### Ejecutar Todas las Herramientas

```bash
# Build completo con todas las validaciones
./gradlew clean build

# Solo herramientas de calidad
./gradlew spotlessCheck checkstyleMain pmdMain spotbugsMain

# Todas las herramientas + tests
./gradlew check
```

**Nota:** El build falla si SpotBugs encuentra bugs de alta severidad.

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Con reporte de coverage
./gradlew test jacocoTestReport

# Tests específicos
./gradlew test --tests FindExampleByDniServiceTest

# Tests con logs detallados
./gradlew test --info
```

### Tipos de Tests

#### 1. Tests Unitarios (JUnit 5 + Mockito)
```java
@ExtendWith(MockitoExtension.class)
class FindExampleByDniServiceTest {
    @Mock private ExampleRepositoryPort repository;
    @InjectMocks private FindExampleByDniService service;

    @Test
    void shouldFindExampleByDni() {
        // Arrange, Act, Assert
    }
}
```

#### 2. Tests de Integración (Testcontainers)
```java
@Testcontainers
@SpringBootTest
class ExampleRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15-alpine");

    @Test
    void shouldPersistExample() {
        // Test con PostgreSQL real en Docker
    }
}
```

#### 3. Tests de API (WebTestClient)
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ExampleControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateExample() {
        webTestClient.post().uri("/api/v1/examples")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated();
    }
}
```

#### 4. HTTP Mocking (WireMock)
```java
@WireMockTest
class ExternalApiTest {
    @Test
    void shouldMockExternalApi() {
        stubFor(get("/external-api")
            .willReturn(ok().withBody("{\"result\":\"success\"}")));
    }
}
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
  jpa:
    show-sql: true
```

### Cobertura de Código

```bash
# Generar reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte
start build/reports/jacoco/test/html/index.html
```

## 📚 Documentación API (Swagger)

La API está documentada con **OpenAPI 3.0**:

- **Swagger UI**: http://localhost:8080/base-api2/swagger-ui.html
- **JSON Spec**: http://localhost:8080/base-api2/api-docs

**Características:**
- 📖 Documentación interactiva de todos los endpoints
- 🧪 Probar APIs directamente desde el navegador
- 📋 Esquemas de Request/Response
- ⚠️ Códigos de error documentados

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

| Código | Descripción | Ejemplo |
|--------|-------------|---------|
| 400 | Bad Request | Validación fallida (nombre vacío, DNI inválido) |
| 404 | Not Found | Recurso no encontrado con el DNI especificado |
| 409 | Conflict | DNI duplicado al intentar crear |
| 500 | Internal Server Error | Error inesperado del servidor |

### Excepciones Personalizadas

```java
// domain/exception/
- BadRequestException         // 400
- ExampleNotFoundException    // 404
- ExampleAlreadyExistsException // 409
- InfrastructureException     // 500
```

## 📦 Base de Datos

### Migraciones con Flyway

Las migraciones se versionan en `src/main/resources/db/migration/`:

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

### Comandos Útiles

```bash
# Conectar a PostgreSQL
docker exec -it baseapi2-postgres psql -U postgres -d baseapi2

# Ver tablas
\dt app.*

# Ver datos
SELECT * FROM app.examples;

# Ver historial de Flyway
SELECT * FROM app.flyway_schema_history;
```

## 🛠️ Comandos Útiles

### Docker Compose

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

# Ver uso de recursos
docker stats baseapi2-app baseapi2-postgres baseapi2-redis
```

### Gradle

```bash
# Limpiar y compilar
./gradlew clean build

# Solo compilar (sin tests)
./gradlew build -x test

# Ejecutar la aplicación
./gradlew bootRun

# Ver dependencias
./gradlew dependencies

# Ver tareas disponibles
./gradlew tasks

# Build en modo verbose
./gradlew build --info
```

## 📊 Arquitectura de Servicios

```
┌─────────────────────────────────┐
│       Docker Host               │
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐  │
│  │    App (Spring Boot)      │  │ :8080
│  │  ┌─────────────────────┐  │  │
│  │  │  LoggingFilter      │  │  │ ◄── Captura requests
│  │  │  LogSanitizer       │  │  │ ◄── Sanitiza datos
│  │  └─────────────────────┘  │  │
│  │  ┌─────────────────────┐  │  │
│  │  │  Controllers        │  │  │
│  │  │  Use Cases          │  │  │
│  │  │  Domain Models      │  │  │
│  │  └─────────────────────┘  │  │
│  └───────┬───────────┬───────┘  │
│          │           │          │
│          ▼           ▼          │
│    ┌──────────┐  ┌──────────┐  │
│    │PostgreSQL│  │  Redis   │  │ :5432, :6379
│    │   15     │  │    7     │  │
│    └──────────┘  └──────────┘  │
│                                 │
└─────────────────────────────────┘
```

## 🎯 Roadmap

### Completado ✅
- [x] Arquitectura hexagonal
- [x] Docker multi-stage build
- [x] Redis cache con TTL
- [x] H2 para tests
- [x] OpenAPI/Swagger
- [x] Spotless automático
- [x] Endpoint búsqueda por DNI
- [x] Sistema de logging con sanitización
- [x] Checkstyle, PMD, SpotBugs
- [x] Testcontainers
- [x] WireMock para HTTP mocking
- [x] Tests unitarios completos

### Pendiente 📋
- [ ] Paginación en listados
- [ ] Endpoints de actualización y eliminación
- [ ] MapStruct para mapeos DTO-Domain
- [ ] Spring Security (JWT)
- [ ] Spring Actuator y métricas
- [ ] Prometheus + Grafana
- [ ] CI/CD con GitHub Actions
- [ ] Jacoco con threshold mínimo
- [ ] Rate limiting con Redis
- [ ] Correlation ID para trazabilidad

## 📖 Documentación Adicional

- [DOCKER.md](DOCKER.md) - Guía completa de Docker
- [REDIS.md](REDIS.md) - Guía de Redis y caché

## 🤝 Contribuir

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Aplicar formato (`./gradlew spotlessApply`)
4. Ejecutar tests (`./gradlew test`)
5. Validar calidad (`./gradlew check`)
6. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
7. Push a la rama (`git push origin feature/nueva-funcionalidad`)
8. Crear Pull Request

## 📝 Licencia

Este proyecto es un template base para desarrollo de APIs REST con Spring Boot.

---

## 🏆 Características Destacadas

### 🔒 Seguridad
- Sanitización automática de datos sensibles en logs
- Usuario no-root en Docker
- Validación de inputs con Bean Validation

### 🚀 Performance
- Cache distribuido con Redis
- Imagen Docker optimizada (~200MB)
- Configuración JVM optimizada

### 🛠️ Calidad
- 4 herramientas de análisis de código
- >80% cobertura de tests
- Tests de integración con Testcontainers

### 📊 Observabilidad
- Logging completo de requests/responses
- Medición de duración de operaciones
- Logs estructurados y seguros

---

⭐ **¿Te fue útil?** Dale una estrella al repositorio

🐛 **¿Encontraste un bug?** Reporta un issue

💡 **¿Tienes una idea?** Contribuye con un PR
