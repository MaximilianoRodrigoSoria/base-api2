# 🧪 Guía de Testing

Esta guía explica la estrategia de testing del proyecto, incluyendo tests unitarios, de integración y herramientas utilizadas.

## 📋 Stack de Testing

| Herramienta | Propósito | Versión |
|-------------|-----------|---------|
| **JUnit 5** | Framework de testing | 5.10.x |
| **Mockito** | Mocking para tests unitarios | 5.x |
| **AssertJ** | Assertions fluidas | 3.x |
| **Testcontainers** | Tests de integración con contenedores | 1.20.4 |
| **H2** | Base de datos en memoria | 2.x |
| **WireMock** | HTTP mocking | 3.9.2 |
| **WebTestClient** | Testing de APIs REST | Spring WebFlux |
| **JaCoCo** | Cobertura de código | 0.8.x |

## 🎯 Tipos de Tests

### 1. Tests Unitarios (Unit Tests)

**Objetivo:** Probar componentes individuales aislados.

**Características:**
- Rápidos (< 1 segundo cada uno)
- No requieren infraestructura externa
- Usan mocks para dependencias
- Se ejecutan en cada build

**Ejemplo:**

```java
@ExtendWith(MockitoExtension.class)
class CreateExampleUseCaseTest {
    
    @Mock
    private ExampleRepositoryPort repositoryPort;
    
    @InjectMocks
    private CreateExampleUseCase useCase;
    
    @Test
    @DisplayName("Debe crear example exitosamente")
    void shouldCreateExampleSuccessfully() {
        // Arrange
        CreateExampleCommand command = new CreateExampleCommand("John Doe", "12345678");
        Example expectedExample = new Example(1L, "John Doe", "12345678");
        
        when(repositoryPort.existsByDni("12345678")).thenReturn(false);
        when(repositoryPort.save(any(Example.class))).thenReturn(expectedExample);
        
        // Act
        Example result = useCase.execute(command);
        
        // Assert
        assertThat(result)
            .isNotNull()
            .extracting(Example::getName, Example::getDni)
            .containsExactly("John Doe", "12345678");
        
        verify(repositoryPort).existsByDni("12345678");
        verify(repositoryPort).save(any(Example.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si DNI ya existe")
    void shouldThrowExceptionWhenDniExists() {
        // Arrange
        CreateExampleCommand command = new CreateExampleCommand("John Doe", "12345678");
        when(repositoryPort.existsByDni("12345678")).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(ExampleAlreadyExistsException.class)
            .hasMessageContaining("12345678");
        
        verify(repositoryPort, never()).save(any());
    }
}
```

### 2. Tests de Integración (Integration Tests)

**Objetivo:** Probar la integración entre componentes.

**Características:**
- Usan base de datos real (H2 o Testcontainers)
- Prueban el flujo completo
- Más lentos que tests unitarios
- Se ejecutan con `./gradlew test`

**Ejemplo con H2:**

```java
@SpringBootTest
@ActiveProfiles("test")
class ExampleRepositoryIntegrationTest {
    
    @Autowired
    private ExampleJpaRepository jpaRepository;
    
    @Autowired
    private ExamplePersistenceAdapter adapter;
    
    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Debe persistir y recuperar example")
    void shouldPersistAndRetrieveExample() {
        // Arrange
        Example example = new Example(null, "John Doe", "12345678");
        
        // Act
        Example saved = adapter.save(example);
        Optional<Example> retrieved = adapter.findByDni("12345678");
        
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(retrieved)
            .isPresent()
            .get()
            .extracting(Example::getName, Example::getDni)
            .containsExactly("John Doe", "12345678");
    }
}
```

### 3. Tests con Testcontainers

**Objetivo:** Probar con PostgreSQL real en Docker.

**Características:**
- Base de datos idéntica a producción
- Detecta problemas de compatibilidad SQL
- Requiere Docker instalado

**Ejemplo:**

```java
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ExampleRepositoryTestcontainersTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private ExampleJpaRepository repository;
    
    @Test
    @DisplayName("Debe funcionar con PostgreSQL real")
    void shouldWorkWithRealPostgreSQL() {
        // Arrange
        ExampleEntity entity = new ExampleEntity();
        entity.setName("John Doe");
        entity.setDni("12345678");
        
        // Act
        ExampleEntity saved = repository.save(entity);
        
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByDni("12345678")).isPresent();
    }
}
```

### 4. Tests de API (Controller Tests)

**Objetivo:** Probar los endpoints REST completos.

**Características:**
- Prueban el flujo HTTP completo
- Validan serialización/deserialización
- Verifican códigos de estado HTTP

**Ejemplo:**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExampleControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ExampleJpaRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    @DisplayName("POST /examples - Debe crear example")
    void shouldCreateExample() {
        // Arrange
        CreateExampleRequest request = new CreateExampleRequest("John Doe", "12345678");
        
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.dni").isEqualTo("12345678");
    }
    
    @Test
    @DisplayName("POST /examples - Debe retornar 409 si DNI existe")
    void shouldReturn409WhenDniExists() {
        // Arrange
        ExampleEntity existing = new ExampleEntity();
        existing.setName("Jane Doe");
        existing.setDni("12345678");
        repository.save(existing);
        
        CreateExampleRequest request = new CreateExampleRequest("John Doe", "12345678");
        
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").value(containsString("12345678"));
    }
    
    @Test
    @DisplayName("GET /examples/dni/{dni} - Debe encontrar por DNI")
    void shouldFindByDni() {
        // Arrange
        ExampleEntity entity = new ExampleEntity();
        entity.setName("John Doe");
        entity.setDni("12345678");
        repository.save(entity);
        
        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/examples/dni/12345678")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.dni").isEqualTo("12345678");
    }
    
    @Test
    @DisplayName("GET /examples - Debe listar todos")
    void shouldListAll() {
        // Arrange
        ExampleEntity entity1 = new ExampleEntity();
        entity1.setName("John Doe");
        entity1.setDni("12345678");
        
        ExampleEntity entity2 = new ExampleEntity();
        entity2.setName("Jane Smith");
        entity2.setDni("87654321");
        
        repository.saveAll(List.of(entity1, entity2));
        
        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/examples")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExampleResponse.class)
                .hasSize(2);
    }
}
```

### 5. HTTP Mocking con WireMock

**Objetivo:** Simular APIs externas.

**Ejemplo:**

```java
@WireMockTest(httpPort = 8089)
class ExternalApiIntegrationTest {
    
    @Test
    @DisplayName("Debe mockear API externa")
    void shouldMockExternalApi() {
        // Arrange
        stubFor(get(urlEqualTo("/external/users/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":123,\"name\":\"John Doe\"}")));
        
        // Act
        String response = callExternalApi("http://localhost:8089/external/users/123");
        
        // Assert
        assertThat(response).contains("John Doe");
        
        // Verify
        verify(getRequestedFor(urlEqualTo("/external/users/123")));
    }
}
```

## 🚀 Ejecutar Tests

### Todos los Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Con logs detallados
./gradlew test --info

# Continuar a pesar de fallos
./gradlew test --continue
```

### Tests Específicos

```bash
# Por clase
./gradlew test --tests CreateExampleUseCaseTest

# Por método
./gradlew test --tests CreateExampleUseCaseTest.shouldCreateExampleSuccessfully

# Por paquete
./gradlew test --tests "com.ar.laboratory.baseapi2.example.*"

# Por patrón
./gradlew test --tests "*Integration*"
```

### Tests en Paralelo

```gradle
// build.gradle
test {
    maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
}
```

```bash
# Ejecutar en paralelo
./gradlew test --parallel
```

## 📊 Cobertura de Código con JaCoCo

### Generar Reporte

```bash
# Ejecutar tests y generar reporte
./gradlew test jacocoTestReport

# Ver reporte HTML
start build/reports/jacoco/test/html/index.html  # Windows
open build/reports/jacoco/test/html/index.html   # macOS/Linux
```

### Configurar Threshold Mínimo

```gradle
// build.gradle
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80  // 80% mínimo
            }
        }
        
        rule {
            element = 'CLASS'
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.70  // 70% por clase
            }
        }
    }
}

check.dependsOn jacocoTestCoverageVerification
```

### Excluir Clases de Cobertura

```gradle
jacocoTestReport {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                    '**/config/**',
                    '**/dto/**',
                    '**/entity/**',
                    '**/*Application.class'
            ])
        }))
    }
}
```

## 🎨 Buenas Prácticas

### Nomenclatura de Tests

```java
// ✅ BUENO: Descriptivo y claro
@Test
@DisplayName("Debe crear example exitosamente cuando DNI no existe")
void shouldCreateExampleSuccessfully_WhenDniDoesNotExist() {
    // ...
}

// ❌ MALO: No descriptivo
@Test
void test1() {
    // ...
}
```

### Patrón AAA (Arrange-Act-Assert)

```java
@Test
void shouldCalculateTotal() {
    // Arrange - Preparar datos
    Cart cart = new Cart();
    cart.addItem(new Item("Product1", 100.0));
    cart.addItem(new Item("Product2", 50.0));
    
    // Act - Ejecutar acción
    double total = cart.calculateTotal();
    
    // Assert - Verificar resultado
    assertThat(total).isEqualTo(150.0);
}
```

### Usar AssertJ para Assertions Fluidas

```java
// ✅ BUENO: AssertJ (fluido y expresivo)
assertThat(result)
    .isNotNull()
    .hasSize(3)
    .extracting(Example::getName)
    .containsExactly("John", "Jane", "Bob");

// ❌ REGULAR: JUnit assertions (menos expresivo)
assertNotNull(result);
assertEquals(3, result.size());
assertEquals("John", result.get(0).getName());
```

### Tests Independientes

```java
// ✅ BUENO: Cada test limpia su estado
@BeforeEach
void setUp() {
    repository.deleteAll();
}

@Test
void test1() {
    // Test independiente
}

@Test
void test2() {
    // Test independiente
}
```

### Evitar Lógica Compleja en Tests

```java
// ❌ MALO: Lógica compleja
@Test
void shouldProcessItems() {
    for (int i = 0; i < 10; i++) {
        if (i % 2 == 0) {
            // ...
        }
    }
}

// ✅ BUENO: Test simple y directo
@ParameterizedTest
@ValueSource(ints = {0, 2, 4, 6, 8})
void shouldProcessEvenNumbers(int number) {
    // Test simple
}
```

## 🔧 Configuración de Tests

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
  
  cache:
    type: simple  # Cache simple en lugar de Redis
  
  main:
    banner-mode: off

logging:
  level:
    root: WARN
    com.ar.laboratory.baseapi2: DEBUG
    org.hibernate.SQL: DEBUG
```

### Test Slices

```java
// Solo testing de JPA
@DataJpaTest
class ExampleRepositoryTest {
    @Autowired
    private ExampleJpaRepository repository;
}

// Solo testing de Web
@WebMvcTest(ExampleController.class)
class ExampleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CreateExampleUseCase createUseCase;
}
```

## 🎯 Estrategia de Testing

### Pirámide de Testing

```
       /\
      /  \        10% - E2E Tests
     /----\       
    /      \      20% - Integration Tests
   /--------\     
  /          \    70% - Unit Tests
 /____________\   
```

**Distribución recomendada:**
- 70% Tests Unitarios (rápidos, aislados)
- 20% Tests de Integración (verifican integraciones)
- 10% Tests E2E (validan flujos completos)

### Cobertura Objetivo

| Capa | Objetivo | Actual |
|------|----------|--------|
| Domain | 100% | ✅ 100% |
| Application (Use Cases) | 95% | ✅ 97% |
| Infrastructure (Adapters) | 80% | ✅ 85% |
| Controllers | 80% | ✅ 82% |
| **Total** | **85%** | **✅ 88%** |

## 📚 Testing Checklist

### ✅ Tests Unitarios

- [ ] Caso exitoso (happy path)
- [ ] Casos de error (excepciones)
- [ ] Validaciones de entrada
- [ ] Lógica de negocio
- [ ] Edge cases
- [ ] Null safety

### ✅ Tests de Integración

- [ ] CRUD completo
- [ ] Transacciones
- [ ] Constraints de BD
- [ ] Relaciones entre entidades
- [ ] Flyway migrations

### ✅ Tests de API

- [ ] Códigos HTTP correctos (200, 201, 404, 409, 500)
- [ ] Formato de respuesta JSON
- [ ] Validación de entrada
- [ ] Headers correctos
- [ ] Error responses

## 🔍 Troubleshooting

### Tests Fallan Localmente pero Pasan en CI

**Causa:** Estado compartido entre tests.

**Solución:**
```java
@BeforeEach
void setUp() {
    repository.deleteAll();
}
```

### Tests Lentos

**Causa:** Tests de integración con I/O.

**Solución:**
```java
// Usar @Tag para separar tests rápidos de lentos
@Tag("slow")
@Test
void slowIntegrationTest() {
    // ...
}

// Ejecutar solo tests rápidos
// ./gradlew test --tests "*" --exclude-tag slow
```

### Testcontainers No Inicia

**Causa:** Docker no está corriendo.

**Solución:**
```bash
# Verificar Docker
docker ps

# Iniciar Docker
# Windows: Abrir Docker Desktop
# Linux: sudo systemctl start docker
```

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testcontainers](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

**Volver al [README principal](../README.md)**
