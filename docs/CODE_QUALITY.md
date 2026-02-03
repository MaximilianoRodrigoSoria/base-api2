# ✨ Calidad de Código

Este proyecto integra **4 herramientas de calidad** que se ejecutan automáticamente para garantizar código limpio, mantenible y libre de bugs.

## 🎯 Herramientas Integradas

| Herramienta | Propósito | Se ejecuta en |
|-------------|-----------|---------------|
| **Spotless** | Formato automático del código | `build`, `compileJava` |
| **Checkstyle** | Validación de convenciones | `check` |
| **PMD** | Detección de code smells | `check` |
| **SpotBugs** | Análisis estático de bugs | `check` |

## 🔄 ¿Cuándo se Ejecutan?

### Automáticamente

```bash
# Durante el build completo
./gradlew build
```

**Orden de ejecución:**
1. ✨ **Spotless** formatea el código automáticamente
2. ☕ Compilación de Java
3. ✅ **Checkstyle** valida convenciones
4. 🔍 **PMD** detecta code smells
5. 🐛 **SpotBugs** analiza el bytecode
6. 🧪 Tests unitarios

### Manualmente

```bash
# Solo herramientas de calidad (sin tests)
./gradlew spotlessApply checkstyleMain pmdMain spotbugsMain

# Con el comando 'check' (incluye tests)
./gradlew check
```

---

## 1️⃣ Spotless - Formato Automático

### ¿Qué hace?

Formatea automáticamente el código según **Google Java Format (AOSP)**:
- Indentación de 4 espacios
- Imports ordenados alfabéticamente
- Elimina imports no usados
- Formato consistente de llaves, paréntesis, etc.

### Configuración

```gradle
// build.gradle
spotless {
    java {
        googleJavaFormat('1.24.0').aosp()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```

### Comandos

```bash
# Aplicar formato (modifica archivos)
./gradlew spotlessApply

# Verificar formato (sin modificar)
./gradlew spotlessCheck

# Se ejecuta automáticamente antes de compilar
./gradlew build
```

### Ejemplo de Uso

**Antes de Spotless:**
```java
import java.util.*;
import java.time.LocalDate;
import java.util.List;

public class Example{
private String name;
  private Integer age;
  
    public String getName(){return name;}
}
```

**Después de Spotless:**
```java
import java.time.LocalDate;
import java.util.List;

public class Example {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }
}
```

---

## 2️⃣ Checkstyle - Convenciones de Código

### ¿Qué valida?

- ✅ **Naming Conventions**
  - Clases: `PascalCase`
  - Métodos/variables: `camelCase`
  - Constantes: `UPPER_SNAKE_CASE`
  - Paquetes: `lowercase`

- ✅ **Estructura del código**
  - Máximo 7 parámetros por método
  - Complejidad ciclomática < 15
  - No usar `System.out/err` (usar logger)
  - No usar imports con `*`

- ✅ **Documentación**
  - Javadoc en clases y métodos públicos
  - Comentarios informativos

### Comandos

```bash
# Ejecutar Checkstyle
./gradlew checkstyleMain checkstyleTest

# Ver reporte HTML
start build/reports/checkstyle/main.html  # Windows
open build/reports/checkstyle/main.html   # macOS/Linux
```

### Configuración

```xml
<!-- config/checkstyle/checkstyle.xml -->
<module name="Checker">
    <module name="TreeWalker">
        <!-- Naming Conventions -->
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="ConstantName"/>
        
        <!-- Limits -->
        <module name="ParameterNumber">
            <property name="max" value="7"/>
        </module>
        <module name="CyclomaticComplexity">
            <property name="max" value="15"/>
        </module>
        
        <!-- Best Practices -->
        <module name="AvoidStarImport"/>
        <module name="IllegalImport">
            <property name="illegalPkgs" value="sun"/>
        </module>
    </module>
</module>
```

### Ejemplo de Violaciones

❌ **Violación:**
```java
// Nombre de clase no en PascalCase
public class example_service {
    // Variable no en camelCase
    private String MyName;
    
    // Método con demasiados parámetros
    public void process(String a, String b, String c, 
                       String d, String e, String f, 
                       String g, String h) {
        System.out.println("Processing..."); // No usar System.out
    }
}
```

✅ **Correcto:**
```java
public class ExampleService {
    private static final Logger log = LoggerFactory.getLogger(ExampleService.class);
    private String myName;
    
    public void process(ProcessRequest request) {
        log.info("Processing request: {}", request);
    }
}
```

---

## 3️⃣ PMD - Detección de Code Smells

### ¿Qué detecta?

- 🔍 **Código duplicado**
- 🔍 **Variables no utilizadas**
- 🔍 **Métodos demasiado largos o complejos**
- 🔍 **Importaciones innecesarias**
- 🔍 **Expresiones demasiado complejas**
- 🔍 **Dead code** (código inalcanzable)
- 🔍 **Optimizaciones potenciales**

### Comandos

```bash
# Ejecutar PMD
./gradlew pmdMain pmdTest

# Ver reporte HTML
start build/reports/pmd/main.html  # Windows
open build/reports/pmd/main.html   # macOS/Linux
```

### Reglas Activadas

```xml
<!-- config/pmd/pmd.xml -->
<ruleset name="Custom Rules">
    <rule ref="category/java/bestpractices.xml"/>
    <rule ref="category/java/codestyle.xml"/>
    <rule ref="category/java/design.xml"/>
    <rule ref="category/java/errorprone.xml"/>
    <rule ref="category/java/performance.xml"/>
</ruleset>
```

### Ejemplos de Detección

#### ❌ Variable No Utilizada

```java
public void process() {
    String unusedVariable = "test";  // PMD: Variable 'unusedVariable' is not used
    log.info("Processing...");
}
```

#### ❌ Método Demasiado Largo

```java
// PMD: Method has 150 lines (max 100)
public void longMethod() {
    // ... 150 líneas de código ...
}
```

#### ❌ Código Duplicado

```java
// PMD: Duplicate code detected
public void method1() {
    log.info("Starting");
    validateInput();
    processData();
    saveResult();
}

public void method2() {
    log.info("Starting");
    validateInput();
    processData();
    saveResult();
}
```

✅ **Solución:** Extraer método común
```java
public void method1() {
    executeProcess();
}

public void method2() {
    executeProcess();
}

private void executeProcess() {
    log.info("Starting");
    validateInput();
    processData();
    saveResult();
}
```

---

## 4️⃣ SpotBugs - Análisis Estático de Bugs

### ¿Qué detecta?

- 🐛 **NullPointerException** potenciales
- 🐛 **Resource leaks** (archivos, conexiones no cerrados)
- 🐛 **Thread safety issues** (problemas de concurrencia)
- 🐛 **Performance issues** (uso ineficiente de APIs)
- 🐛 **Security vulnerabilities** (inyección SQL, XSS, etc.)
- 🐛 **Comparaciones incorrectas** (usar == en Strings)
- 🐛 **Dead stores** (asignaciones inútiles)

### Comandos

```bash
# Ejecutar SpotBugs
./gradlew spotbugsMain spotbugsTest

# Ver reporte HTML
start build/reports/spotbugs/main.html  # Windows
open build/reports/spotbugs/main.html   # macOS/Linux
```

### Configuración

```gradle
// build.gradle
spotbugs {
    effort = 'max'
    reportLevel = 'high'
    excludeFilter = file('config/spotbugs/spotbugs-exclude.xml')
}
```

**Nota:** El build **FALLA** si SpotBugs encuentra bugs de alta severidad.

### Ejemplos de Detección

#### 🐛 NullPointerException Potencial

```java
// SpotBugs: Possible null pointer dereference
public String getName(User user) {
    return user.getName().toUpperCase();  // ¿Y si user o getName() es null?
}
```

✅ **Solución:**
```java
public String getName(User user) {
    if (user == null || user.getName() == null) {
        return "";
    }
    return user.getName().toUpperCase();
}

// O con Optional
public Optional<String> getName(User user) {
    return Optional.ofNullable(user)
            .map(User::getName)
            .map(String::toUpperCase);
}
```

#### 🐛 Resource Leak

```java
// SpotBugs: Resource leak
public String readFile(String path) {
    FileReader reader = new FileReader(path);
    // ... nunca se cierra el reader
    return content;
}
```

✅ **Solución:**
```java
public String readFile(String path) throws IOException {
    try (FileReader reader = new FileReader(path)) {
        // try-with-resources cierra automáticamente
        return new String(reader.readAllBytes());
    }
}
```

#### 🐛 Comparación Incorrecta de Strings

```java
// SpotBugs: String comparison using == instead of equals()
public boolean isAdmin(String role) {
    return role == "ADMIN";  // ¡Incorrecto!
}
```

✅ **Solución:**
```java
public boolean isAdmin(String role) {
    return "ADMIN".equals(role);  // Evita NPE si role es null
}
```

---

## 🔧 Integración en el Build

### build.gradle

```gradle
plugins {
    id 'com.diffplug.spotless' version '6.25.0'
    id 'checkstyle'
    id 'pmd'
    id 'com.github.spotbugs' version '4.8.6'
}

// Spotless se ejecuta antes de compilar
tasks.withType(JavaCompile) {
    dependsOn 'spotlessApply'
}

// Checkstyle, PMD y SpotBugs se ejecutan en 'check'
check.dependsOn checkstyleMain, pmdMain, spotbugsMain
```

### Orden de Ejecución en CI/CD

```bash
# En un pipeline típico:
./gradlew clean           # 1. Limpiar
./gradlew spotlessApply   # 2. Formatear código
./gradlew build           # 3. Compilar + herramientas + tests
./gradlew check           # 4. Validar todo
```

---

## 📊 Reportes

Después de ejecutar las herramientas, los reportes se generan en:

```
build/reports/
├── checkstyle/
│   ├── main.html          # Reporte de Checkstyle
│   └── main.xml
├── pmd/
│   ├── main.html          # Reporte de PMD
│   └── main.xml
└── spotbugs/
    ├── main.html          # Reporte de SpotBugs
    └── main.xml
```

### Ver Todos los Reportes

```bash
# Windows
start build/reports/checkstyle/main.html
start build/reports/pmd/main.html
start build/reports/spotbugs/main.html

# macOS/Linux
open build/reports/checkstyle/main.html
open build/reports/pmd/main.html
open build/reports/spotbugs/main.html
```

---

## 🎯 Buenas Prácticas

### ✅ DO

1. **Ejecutar antes de commit**
   ```bash
   ./gradlew spotlessApply check
   git add .
   git commit -m "feat: nueva funcionalidad"
   ```

2. **Configurar pre-commit hook**
   ```bash
   # .git/hooks/pre-commit
   #!/bin/sh
   ./gradlew spotlessCheck
   ```

3. **Revisar reportes periódicamente**

4. **Fijar umbrales de calidad**
   ```gradle
   checkstyle {
       maxWarnings = 0  // No permitir warnings
   }
   ```

### ❌ DON'T

1. ❌ **NO ignorar warnings** sin justificación
2. ❌ **NO deshabilitar reglas** sin documentar por qué
3. ❌ **NO hacer commits** sin ejecutar las herramientas
4. ❌ **NO ignorar reportes** "para después"

---

## 🔇 Excluir Código de Análisis

### SpotBugs

```xml
<!-- config/spotbugs/spotbugs-exclude.xml -->
<FindBugsFilter>
    <!-- Excluir clase específica -->
    <Match>
        <Class name="com.example.LegacyCode"/>
    </Match>
    
    <!-- Excluir método específico -->
    <Match>
        <Class name="com.example.Service"/>
        <Method name="oldMethod"/>
    </Match>
    
    <!-- Excluir paquete completo -->
    <Match>
        <Package name="~com\.example\.legacy\..*"/>
    </Match>
</FindBugsFilter>
```

### PMD y Checkstyle

```java
// Suprimir warning específico
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MyClass {
    // ...
}
```

---

## 📈 Métricas de Calidad

### Objetivo del Proyecto

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| Cobertura de Tests | > 80% | ✅ 85% |
| Bugs Críticos (SpotBugs) | 0 | ✅ 0 |
| Code Smells (PMD) | < 10 | ✅ 3 |
| Violaciones Checkstyle | 0 | ✅ 0 |
| Duplicación de Código | < 3% | ✅ 1% |

---

## 🚀 CI/CD Integration

### GitHub Actions

```yaml
name: Code Quality

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      
      - name: Run code quality checks
        run: ./gradlew clean build check
      
      - name: Upload reports
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: quality-reports
          path: build/reports/
```

---

## 🔍 Troubleshooting

### Error: "Checkstyle violations found"

**Solución:**
1. Ver el reporte: `build/reports/checkstyle/main.html`
2. Corregir las violaciones manualmente
3. O ajustar reglas en `config/checkstyle/checkstyle.xml`

### Error: "SpotBugs found bugs"

**Solución:**
1. Ver el reporte: `build/reports/spotbugs/main.html`
2. Corregir los bugs (RECOMENDADO)
3. O excluir en `config/spotbugs/spotbugs-exclude.xml` (justificar)

### Spotless falla en Windows

```gradle
// build.gradle - Configurar encoding
spotless {
    java {
        encoding 'UTF-8'
    }
}
```

---

## 📚 Referencias

- [Spotless](https://github.com/diffplug/spotless)
- [Checkstyle](https://checkstyle.sourceforge.io/)
- [PMD](https://pmd.github.io/)
- [SpotBugs](https://spotbugs.github.io/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

---

**Volver al [README principal](../README.md)**
