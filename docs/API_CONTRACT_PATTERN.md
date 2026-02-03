# Patrón de Contratos API - Documentación OpenAPI

## 📋 Descripción

Este proyecto utiliza el **patrón de Interfaces para separar documentación de implementación** en los Controllers REST. Toda la documentación OpenAPI/Swagger está centralizada en interfaces dedicadas, dejando los Controllers limpios y enfocados en la lógica de negocio.

## 🎯 Objetivos Logrados

- ✅ **Controllers limpios y legibles** - Sin ruido de anotaciones Swagger
- ✅ **Documentación centralizada** - Toda en las interfaces API
- ✅ **Mantenibilidad mejorada** - Cambios en documentación no afectan implementación
- ✅ **Reutilización** - Meta-anotaciones para errores estándar
- ✅ **Compatibilidad total** - Con SpringDoc OpenAPI

## 🏗️ Estructura

```
infrastructure/adapter/in/web/
├── api/                           # 📁 Contratos y documentación
│   ├── StandardApiResponses.java # 🏷️  Meta-anotación errores estándar
│   ├── ExampleApi.java           # 📄 Contrato + Docs Examples
│   └── CallHistoryApi.java       # 📄 Contrato + Docs Call History
│
├── ExampleController.java        # 🎯 Implementación limpia
└── CallHistoryController.java    # 🎯 Implementación limpia
```

## 📝 Componentes

### 1. Meta-Anotación para Errores Estándar

**Archivo:** `StandardApiResponses.java`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
    @ApiResponse(responseCode = "400", description = "Solicitud inválida o error de validación"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
public @interface StandardApiResponses {}
```

**Beneficio:** Evita repetir las mismas respuestas de error en cada endpoint.

### 2. Interfaz API (Contrato + Documentación)

**Archivo:** `ExampleApi.java`

```java
@Tag(name = "Examples", description = "API para gestión de ejemplos")
public interface ExampleApi {

    @Operation(summary = "Crear un nuevo ejemplo", ...)
    @ApiResponses(...)
    @StandardApiResponses
    ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request);

    @Operation(summary = "Listar todos los ejemplos", ...)
    @ApiResponses(...)
    @StandardApiResponses
    ResponseEntity<List<ExampleResponse>> listAll();

    // ... más métodos
}
```

**Contiene:**
- ✅ `@Tag` - Agrupación en Swagger
- ✅ `@Operation` - Descripción del endpoint
- ✅ `@ApiResponses` - Respuestas HTTP
- ✅ `@Parameter` - Descripción de parámetros
- ✅ Firma completa del método

### 3. Controller (Implementación Limpia)

**Archivo:** `ExampleController.java`

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
public class ExampleController implements ExampleApi {

    private final CreateExampleUseCase createExampleUseCase;
    private final ListExamplesUseCase listExamplesUseCase;
    private final FindExampleByDniUseCase findExampleByDniUseCase;

    @PostMapping
    @Override
    public ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request) {
        log.info("Request POST /examples: {}", request);
        ExampleResponse response = createExampleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ... más implementaciones
}
```

**Solo contiene:**
- ✅ `@RestController` + `@RequestMapping`
- ✅ Mappings (`@GetMapping`, `@PostMapping`, etc.)
- ✅ Lógica de negocio
- ✅ Logs
- ❌ Sin anotaciones Swagger

## 🔄 Flujo de Desarrollo

### Para agregar un nuevo endpoint:

1. **Definir en la interfaz** (Documentación):
   ```java
   // En ExampleApi.java
   @Operation(summary = "Actualizar ejemplo")
   @ApiResponses(...)
   @StandardApiResponses
   ResponseEntity<ExampleResponse> update(@PathVariable Long id, @RequestBody UpdateRequest req);
   ```

2. **Implementar en el controller** (Lógica):
   ```java
   // En ExampleController.java
   @PutMapping("/{id}")
   @Override
   public ResponseEntity<ExampleResponse> update(@PathVariable Long id, @RequestBody UpdateRequest req) {
       ExampleResponse response = updateUseCase.update(id, req);
       return ResponseEntity.ok(response);
   }
   ```

## 📊 Comparación: Antes vs Después

### ❌ Antes (Controller con ruido)

```java
@RestController
@RequestMapping("/api/v1/examples")
@Tag(name = "Examples", description = "API para gestión de ejemplos")
public class ExampleController {

    @Operation(
        summary = "Crear un nuevo ejemplo",
        description = "Crea un nuevo ejemplo con nombre y DNI únicos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ejemplo creado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ExampleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request) {
        // 50 líneas de anotaciones para ver estas 3 líneas de código
        ExampleResponse response = createExampleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### ✅ Después (Controller limpio)

```java
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
public class ExampleController implements ExampleApi {

    private final CreateExampleUseCase createExampleUseCase;

    @PostMapping
    @Override
    public ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request) {
        ExampleResponse response = createExampleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**Resultado:** Controller 70% más pequeño y 100% más legible.

## ✨ Ventajas del Patrón

### 1. **Separación de Responsabilidades**
- **Interfaces**: Contrato y documentación
- **Controllers**: Implementación y lógica

### 2. **Mantenibilidad**
- Cambios en documentación → Solo modificar interfaz
- Cambios en implementación → Solo modificar controller

### 3. **Legibilidad**
- Controllers fáciles de leer y entender
- Documentación concentrada y organizada

### 4. **Reutilización**
- Meta-anotaciones evitan duplicación
- Contratos reutilizables entre diferentes implementaciones

### 5. **Testabilidad**
- Mock de interfaces más sencillo
- Tests enfocados en lógica, no en documentación

### 6. **Escalabilidad**
- Fácil agregar nuevos endpoints
- Estructura clara para proyectos grandes

## 🎨 Buenas Prácticas Aplicadas

### ✅ Nombres Claros y Semánticos
- `ExampleApi`, `CallHistoryApi` → Interfaces de contrato
- `ExampleController`, `CallHistoryController` → Implementaciones

### ✅ Paquetización Adecuada
- `api/` → Contratos y documentación
- `web/` → Implementaciones REST

### ✅ Sin Duplicación
- `@StandardApiResponses` para errores comunes

### ✅ Documentación Completa
- Cada endpoint completamente documentado
- Ejemplos en parámetros

### ✅ Compatible con Spring
- Spring detecta automáticamente las anotaciones en interfaces
- SpringDoc OpenAPI genera documentación correctamente

## 🚀 Para Replicar en Otros Proyectos

1. **Crear paquete `api/`** en `infrastructure/adapter/in/web/`
2. **Crear `StandardApiResponses.java`** con errores comunes
3. **Por cada controller existente:**
   - Crear interfaz `XxxApi` con documentación Swagger
   - Refactorizar controller para implementar la interfaz
   - Mover todas las anotaciones Swagger a la interfaz
   - Dejar solo mappings y lógica en el controller
4. **Verificar** que Swagger sigue funcionando

## 📚 Archivos Modificados

### Nuevos:
- `StandardApiResponses.java` (Meta-anotación)
- `ExampleApi.java` (Contrato + Docs)
- `CallHistoryApi.java` (Contrato + Docs)

### Refactorizados:
- `ExampleController.java` (Limpio)
- `CallHistoryController.java` (Limpio)

## 🔍 Verificación

1. **Compilar**: `./gradlew clean build`
2. **Ejecutar**: `./gradlew bootRun`
3. **Swagger UI**: `http://localhost:8080/swagger-ui.html`
4. **Verificar**: Documentación completa y funcional

## 📖 Referencias

- [Spring REST Docs](https://spring.io/projects/spring-restdocs)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Clean Code Practices](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

---

**Patrón implementado con éxito** ✅
