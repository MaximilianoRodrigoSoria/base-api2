# Implementación de Caché con Redis - TTL 30 segundos

## Descripción

Implementación de caché con Redis y Spring Cache para endpoints de lectura, con TTL fijo de 30 segundos, arquitectura limpia y logging de CACHE_HIT/CACHE_MISS.

## Características

- ✅ TTL fijo de 30 segundos por caché
- ✅ Cache key determinístico (incluye clase, método y parámetros)
- ✅ Serialización JSON con Jackson (sin JDK serialization)
- ✅ Logging de CACHE_HIT y CACHE_MISS mediante AOP
- ✅ Configuración habilitada/deshabilitada por property: `app.cache.enabled`
- ✅ Thread-safe y concurrente
- ✅ Implementado en capa de servicio (no en controllers)
- ✅ Tests automatizados incluidos

## Arquitectura

```
┌─────────────────┐
│   Controller    │  <- Sin lógica de caché
└────────┬────────┘
         │
         v
┌─────────────────┐
│  Use Case /     │  <- @Cacheable aplicado aquí
│  Service        │
└────────┬────────┘
         │
         v
┌─────────────────┐
│   Repository    │
└─────────────────┘

Aspectos AOP:
- CacheLoggingAspect: Intercepta @Cacheable para logging
```

## Configuración

### 1. application.yml

```yaml
app:
  cache:
    enabled: true  # Habilitar/deshabilitar caché

spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60000
  
  cache:
    type: redis
    redis:
      time-to-live: 600000 # TTL por defecto: 10 minutos
```

### 2. Cachés configurados

El sistema define los siguientes cachés con TTL de 30 segundos:

| Cache Name | TTL | Uso |
|------------|-----|-----|
| `examplesCache` | 30s | Listado de examples |
| `examplesByDni` | 30s | Búsqueda por DNI |
| `callHistoryCache` | 30s | Historial de llamadas |
| `cache30s` | 30s | Cache genérico 30s |

## Uso en Servicios

### Ejemplo 1: Cachear método sin parámetros

```java
@Service
@RequiredArgsConstructor
public class ListExamplesService implements ListExamplesUseCase {
    
    private final ExampleRepositoryPort repository;
    
    @Override
    @Cacheable(value = CacheConfig.EXAMPLES_CACHE, key = "'all'")
    @Transactional(readOnly = true)
    public List<ExampleResponse> listAll() {
        // Este método se ejecutará solo la primera vez
        // Las siguientes llamadas retornarán desde caché por 30 segundos
        return repository.findAll()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
```

### Ejemplo 2: Cachear método con parámetros

```java
@Service
@RequiredArgsConstructor
public class FindExampleByDniService implements FindExampleByDniUseCase {
    
    private final ExampleRepositoryPort repository;
    
    @Override
    @Cacheable(value = CacheConfig.EXAMPLES_BY_DNI, key = "#dni")
    @Transactional(readOnly = true)
    public ExampleResponse findByDni(String dni) {
        // Cache key será el valor de 'dni'
        // Dos llamadas con mismo DNI retornarán caché
        return repository.findByDni(dni)
            .map(this::toResponse)
            .orElseThrow(() -> new ExampleNotFoundException("DNI not found: " + dni));
    }
}
```

### Ejemplo 3: Cachear método con múltiples parámetros

```java
@Service
@RequiredArgsConstructor
public class ListCallHistoryService implements ListCallHistoryUseCase {
    
    private final CallHistoryRepositoryPort repository;
    
    @Override
    @Cacheable(
        value = CacheConfig.CALL_HISTORY_CACHE, 
        key = "'listAll:' + #limit + ':' + #offset"
    )
    public List<CallHistoryResponse> listAll(int limit, int offset) {
        // Cache key: "listAll:10:0" para limit=10, offset=0
        // Garantiza claves únicas por combinación de parámetros
        return repository.findAll(limit, offset)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
```

## Logging

### Configuración de Logging

```yaml
logging:
  level:
    com.ar.laboratory.baseapi2.infrastructure.cache: DEBUG
```

### Ejemplos de Logs

**CACHE_MISS** (primera llamada):
```
INFO  c.a.l.b.i.cache.CacheLoggingAspect - CACHE_MISS - cache: examplesCache, method: ListExamplesService.listAll, args: [], duration: 45ms
```

**CACHE_HIT** (segunda llamada, desde caché):
```
INFO  c.a.l.b.i.cache.CacheLoggingAspect - CACHE_HIT - cache: examplesCache, method: ListExamplesService.listAll, args: [], duration: 2ms
```

**CACHE_ERROR** (si Redis falla):
```
ERROR c.a.l.b.i.cache.CacheLoggingAspect - CACHE_ERROR - cache: examplesCache, method: ListExamplesService.listAll, args: [], duration: 5ms, error: Connection refused
```

## Tests

### Ejecutar Tests de Caché

```bash
# Ejecutar solo tests de caché
./gradlew test --tests "CacheIntegrationTest"

# Ejecutar todos los tests
./gradlew test
```

### Tests Implementados

1. **testRepositoryMock**: Verifica que el mock funciona correctamente
2. **testServiceWithoutCache**: Valida comportamiento sin caché activo
3. **testServiceWithDifferentDnis**: Verifica que diferentes keys generan diferentes cachés
4. **testListAllService**: Valida cacheo de métodos sin parámetros

### Estructura de Test

```java
@ExtendWith(MockitoExtension.class)
class CacheIntegrationTest {
    
    @Mock
    private ExampleRepositoryPort repository;
    
    @Test
    @DisplayName("CACHE_HIT: Dos llamadas consecutivas ejecutan repositorio 1 sola vez")
    void testCacheHit() {
        // Given
        when(repository.findByDni("12345678"))
            .thenReturn(Optional.of(mockExample));
        
        // When
        service.findByDni("12345678"); // CACHE_MISS
        service.findByDni("12345678"); // CACHE_HIT
        
        // Then
        verify(repository, times(1)).findByDni("12345678");
    }
}
```

## Configuración por Ambiente

### Desarrollo Local

```yaml
# application-local.yml
app:
  cache:
    enabled: true  # Activar caché en desarrollo
```

### Tests

```yaml
# application-test.yml
app:
  cache:
    enabled: false  # Desactivar caché en tests unitarios

spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
```

### Producción

```yaml
# application-prod.yml
app:
  cache:
    enabled: true  # Activar caché en producción

spring:
  data:
    redis:
      host: ${REDIS_HOST:redis-prod.example.com}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
```

## Invalidación Manual de Caché

### Usando @CacheEvict

```java
@Service
@RequiredArgsConstructor
public class CreateExampleService {
    
    @CacheEvict(
        value = CacheConfig.EXAMPLES_CACHE, 
        allEntries = true
    )
    public ExampleResponse create(CreateExampleRequest request) {
        // Al crear un nuevo example, invalidar cache de listado
        return repository.save(example);
    }
}
```

### Usando CacheManager

```java
@Service
@RequiredArgsConstructor
public class CacheService {
    
    private final CacheManager cacheManager;
    
    public void clearAllCaches() {
        cacheManager.getCacheNames()
            .forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                }
            });
    }
    
    public void clearSpecificCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
```

## Monitoreo

### Métricas Recomendadas

1. **Hit Rate**: % de CACHE_HIT vs total de llamadas
2. **Miss Rate**: % de CACHE_MISS vs total de llamadas
3. **Latencia**: Tiempo de respuesta con/sin caché
4. **Errores**: Cantidad de CACHE_ERROR

### Queries de Log

```bash
# Buscar cache hits en logs
grep "CACHE_HIT" application.log

# Buscar cache misses
grep "CACHE_MISS" application.log

# Buscar errores de caché
grep "CACHE_ERROR" application.log

# Contar hit rate
grep "CACHE_HIT\|CACHE_MISS" application.log | wc -l
```

## Troubleshooting

### Problema: Caché no funciona

**Verificar:**
1. ✅ `app.cache.enabled=true` en application.yml
2. ✅ Redis está corriendo: `redis-cli ping` -> `PONG`
3. ✅ Profile activo no es "test"
4. ✅ @Cacheable está en método público
5. ✅ Servicio es un @Bean de Spring (para que funcione el proxy AOP)

### Problema: Datos desactualizados

**Causa:** TTL de 30 segundos puede mantener datos obsoletos

**Soluciones:**
1. Usar @CacheEvict en operaciones de escritura
2. Reducir TTL si es necesario
3. Implementar invalidación manual

### Problema: Redis Connection Refused

**Verificar:**
```bash
# Verificar si Redis está corriendo
docker ps | grep redis

# Iniciar Redis si no está corriendo
docker-compose up -d redis

# Verificar conectividad
redis-cli -h localhost -p 6379 ping
```

## Mejores Prácticas

1. **❌ No cachear:**
   - Datos que cambian frecuentemente (< 30 segundos)
   - Datos sensibles/personales
   - Operaciones de escritura

2. **✅ Sí cachear:**
   - Catálogos/listas que cambian poco
   - Datos consultados frecuentemente
   - Cálculos costosos

3. **Keys de caché:**
   - Usar nombres descriptivos
   - Incluir todos los parámetros relevantes
   - Mantener keys cortos pero únicos

4. **Monitoring:**
   - Revisar logs de CACHE_HIT/MISS regularmente
   - Ajustar TTL según necesidad
   - Monitorear uso de memoria en Redis

## Referencias

- Spring Cache: https://docs.spring.io/spring-framework/reference/integration/cache.html
- Redis: https://redis.io/docs/
- Spring Data Redis: https://spring.io/projects/spring-data-redis

## Changelog

- **v1.0.0** (2026-02-03): Implementación inicial
  - TTL 30 segundos
  - Logging CACHE_HIT/CACHE_MISS
  - Tests automatizados
  - Configuración por property
