Perfecto. A continuación tenés un **README directo, autocontenido y descargable**, pensado como **contrato de construcción del proyecto**.
No es explicativo ni opinable: **define reglas**.
Podés copiarlo tal cual a `README.md`.

---

# Contrato de Arquitectura del Proyecto

## Arquitectura Hexagonal por Feature (Reglas Obligatorias)

Este documento define **las reglas de construcción del proyecto**.
No son recomendaciones. **Son obligatorias**.

Cualquier código que no cumpla estas reglas **debe refactorizarse**.

---

## 1. Principio rector (regla de oro)

> **Nada que esté en `infrastructure` puede ser usado por `application`.**
> **Nada que esté en `application` o `infrastructure` puede contaminar `domain`.**

Si esta regla se rompe, **la arquitectura está rota**.

---

## 2. Organización general del proyecto

El proyecto se organiza **por feature**, no por capas globales.

❌ Prohibido:

```
application/
domain/
infrastructure/
```

✅ Obligatorio:

```
feature/
 ├─ domain/
 ├─ application/
 └─ infrastructure/
```

Ejemplo real:

```
example/
callhistory/
user/
```

---

## 3. Estructura obligatoria por feature

Cada feature debe contener **exactamente** estas capas:

```
<feature-name>/
 ├─ domain/
 ├─ application/
 │  ├─ inbound/
 │  │  └─ command/
 │  ├─ outbound/
 │  │  └─ port/
 │  └─ usecase/
 └─ infrastructure/
    ├─ inbound/
    │  └─ web/
    ├─ outbound/
    │  └─ persistence/
    └─ config/
```

No se permiten capas adicionales.

---

## 4. Domain (núcleo del negocio)

### Responsabilidad

Representa el **modelo puro del dominio**.

### Contiene

* Modelos de dominio
* Value Objects
* Excepciones propias del dominio
* Reglas de negocio

### Prohibiciones absolutas

* ❌ Spring
* ❌ JPA / Hibernate
* ❌ DTOs (Request / Response)
* ❌ Controllers
* ❌ Repositories
* ❌ Imports de `application` o `infrastructure`

### Regla clave

> El **Domain no sabe** cómo se persiste, cómo se expone ni desde dónde se invoca.

---

## 5. Application (casos de uso)

### Responsabilidad

Define **qué hace el sistema**, no cómo lo hace técnicamente.

Application **orquesta el dominio mediante contratos**.

---

### 5.1 Inbound – Commands

* Son **interfaces**
* Representan una intención del negocio
* Un comando = un caso de uso
* Un único método: `execute(...)`

Naming obligatorio:

```
XxxCommand
```

Ejemplo:

```java
public interface CreateExampleCommand {
    Example execute(CreateExampleInput input);
}
```

---

### 5.2 UseCase

* Implementan un `Command`
* Contienen la lógica del caso de uso
* Usan solo:

    * Domain
    * Puertos de salida (Outbound)

Naming obligatorio:

```
XxxUseCase
```

---

### 5.3 Outbound – Ports

* Son **interfaces**
* Representan dependencias externas (DB, APIs, colas, etc.)

Naming obligatorio:

```
XxxPort
XxxRepositoryPort
```

---

### Prohibiciones en Application

* ❌ `@Service`
* ❌ `@Component`
* ❌ `@Configuration`
* ❌ `@Transactional`
* ❌ `@Cacheable`
* ❌ JPA / Redis / Kafka / HTTP
* ❌ Imports desde `infrastructure`

> **Application no conoce Spring.**

---

## 6. Infrastructure (detalles técnicos)

### Responsabilidad

Resuelve el **cómo técnico**:

* HTTP
* Persistencia
* Frameworks
* Configuración
* Cache
* AOP
* Async
* Logging

---

### 6.1 Inbound Infrastructure (Web)

* Controllers
* APIs OpenAPI
* DTOs Request / Response
* Mappers DTO ↔ Domain

Regla:

> El controller **solo delega** a un Command.

---

### 6.2 Outbound Infrastructure (Persistence / Clients)

* Adapters
* Entities JPA
* Repositories framework
* Mappers Domain ↔ Entity

Naming obligatorio:

```
XxxAdapter
XxxEntity
XxxJpaRepository
```

---

### 6.3 Configuración

* Wiring de beans
* Decoración técnica (cache, tx, async)
* Aquí se aplican:

    * `@Transactional`
    * `@Cacheable`
    * `@Async`

> **Nunca en Application.**

---

## 7. Dependencias permitidas

| Capa           | Puede depender de    |
| -------------- | -------------------- |
| Domain         | Nadie                |
| Application    | Domain               |
| Infrastructure | Application + Domain |

### Dependencias prohibidas

* ❌ Application → Infrastructure
* ❌ Domain → Application
* ❌ Domain → Infrastructure

---

## 8. Código compartido (`shared`)

### Regla general

> **Evitarlo.**

Si existe, solo puede contener **infraestructura transversal**:

Permitido:

* logging
* filtros
* AOP
* cache
* configuración
* exception handler web

Prohibido:

* modelos de dominio
* DTOs de negocio
* lógica de casos de uso

---

## 9. Naming obligatorio

| Tipo       | Sufijo       |
| ---------- | ------------ |
| Command    | `Command`    |
| UseCase    | `UseCase`    |
| Port       | `Port`       |
| Adapter    | `Adapter`    |
| Entity     | `Entity`     |
| Controller | `Controller` |

---

## 10. Criterio de validación del proyecto

Un proyecto **cumple** si:

* Cada feature es autónomo
* Domain es puro
* Application no usa framework
* Infrastructure implementa puertos
* No existen imports prohibidos

Si **una sola** de estas reglas se rompe:

> **Es necesario refactor.**

---

## 11. Regla final (no negociable)

> **Este README es el contrato arquitectónico del proyecto.**
> No se interpreta.
> No se flexibiliza.
> Se cumple.

