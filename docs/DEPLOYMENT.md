# 🚀 Guía de Despliegue con Docker Compose

Esta guía explica cómo desplegar la aplicación utilizando Docker y Docker Compose.

## 📋 Requisitos Previos

- ✅ **Docker** 20.10+ ([Instalar Docker](https://docs.docker.com/get-docker/))
- ✅ **Docker Compose** 2.0+ (incluido con Docker Desktop)
- ✅ **8GB RAM** mínimo recomendado
- ✅ **Puertos disponibles:** 8080 (app), 5432 (PostgreSQL), 6379 (Redis)

## 🏗️ Arquitectura de Servicios

```yaml
┌─────────────────────────────────────────┐
│         Docker Compose Stack            │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  baseapi2-app (Spring Boot)      │  │
│  │  - Puerto: 8080                  │  │
│  │  - Imagen: baseapi2:latest       │  │
│  │  - JRE 21 Alpine (~200MB)        │  │
│  └──────┬────────────────────┬──────┘  │
│         │                    │         │
│         ▼                    ▼         │
│  ┌────────────┐      ┌──────────────┐ │
│  │ PostgreSQL │      │    Redis     │ │
│  │  - 5432    │      │    - 6379    │ │
│  │  - Vol: db │      │  - Vol: cache│ │
│  └────────────┘      └──────────────┘ │
└─────────────────────────────────────────┘
```

## 🐳 Servicios

### 1. baseapi2-app (Aplicación)

**Características:**
- Imagen optimizada multi-stage (~200MB)
- Usuario no-root (spring:spring)
- Health checks configurados
- Restart automático en caso de fallo

**Configuración:**
```yaml
app:
  image: baseapi2:latest
  container_name: baseapi2-app
  ports:
    - "8080:8080"
  environment:
    SPRING_PROFILES_ACTIVE: local
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/baseapi2
    SPRING_DATA_REDIS_HOST: redis
    JAVA_OPTS: "-Xmx512m -Xms256m"
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_started
```

### 2. PostgreSQL 15

**Características:**
- Base de datos principal
- Volumen persistente
- Health check con pg_isready
- Configuración optimizada

**Configuración:**
```yaml
postgres:
  image: postgres:15-alpine
  container_name: baseapi2-postgres
  environment:
    POSTGRES_DB: baseapi2
    POSTGRES_USER: postgres
    POSTGRES_PASSWORD: postgres
  ports:
    - "5432:5432"
  volumes:
    - postgres-data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U postgres"]
    interval: 10s
    timeout: 5s
    retries: 5
```

### 3. Redis 7

**Características:**
- Caché distribuido
- Volumen persistente
- Configuración de memoria

**Configuración:**
```yaml
redis:
  image: redis:7-alpine
  container_name: baseapi2-redis
  ports:
    - "6379:6379"
  volumes:
    - redis-data:/data
  command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

## 🚀 Despliegue

### Opción 1: Despliegue Completo (Recomendado)

```bash
# Clonar el repositorio (si no lo tienes)
git clone https://github.com/tu-usuario/base-api2.git
cd base-api2

# Levantar todos los servicios
docker-compose up --build

# En modo detached (segundo plano)
docker-compose up --build -d
```

**Proceso:**
1. 🏗️ Construye la imagen de la aplicación (multi-stage)
2. 🐘 Levanta PostgreSQL y espera health check
3. 🔴 Levanta Redis
4. ☕ Inicia la aplicación Spring Boot
5. 📦 Flyway ejecuta migraciones automáticamente
6. ✅ Aplicación lista en http://localhost:8080/base-api2

### Opción 2: Solo Infraestructura

```bash
# Levantar solo PostgreSQL y Redis
docker-compose up postgres redis -d

# Ejecutar la aplicación localmente
./gradlew bootRun
```

**Uso:** Útil para desarrollo local con hot-reload.

### Opción 3: Usando Imagen Pre-construida

```bash
# Construir imagen primero
docker build -t baseapi2:latest .

# Levantar servicios
docker-compose up -d
```

## 📊 Verificar Despliegue

### 1. Estado de Servicios

```bash
# Ver estado de todos los servicios
docker-compose ps

# Salida esperada:
# NAME               STATUS        PORTS
# baseapi2-app       Up (healthy)  0.0.0.0:8080->8080/tcp
# baseapi2-postgres  Up (healthy)  0.0.0.0:5432->5432/tcp
# baseapi2-redis     Up            0.0.0.0:6379->6379/tcp
```

### 2. Health Check de la Aplicación

```bash
# Verificar health endpoint
curl http://localhost:8080/base-api2/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

### 3. Probar Endpoints

```bash
# Listar examples
curl http://localhost:8080/base-api2/api/v1/examples

# Crear example
curl -X POST http://localhost:8080/base-api2/api/v1/examples \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","dni":"12345678"}'
```

### 4. Swagger UI

Abrir en el navegador:
- **Swagger UI:** http://localhost:8080/base-api2/swagger-ui.html

## 📋 Logs

### Ver Logs en Tiempo Real

```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app

# Solo PostgreSQL
docker-compose logs -f postgres

# Solo Redis
docker-compose logs -f redis

# Últimas 100 líneas
docker-compose logs --tail=100 app
```

### Logs de Migraciones Flyway

```bash
# Ver logs de Flyway al iniciar
docker-compose logs app | grep -i flyway
```

## 🔧 Gestión de Servicios

### Iniciar/Detener

```bash
# Iniciar servicios
docker-compose start

# Detener servicios (preserva contenedores y volúmenes)
docker-compose stop

# Reiniciar servicios
docker-compose restart

# Reiniciar solo la app
docker-compose restart app
```

### Detener y Eliminar

```bash
# Detener y eliminar contenedores (preserva volúmenes)
docker-compose down

# Detener y eliminar TODO (incluyendo volúmenes)
docker-compose down -v

# Eliminar imágenes también
docker-compose down --rmi all -v
```

## 🔄 Reconstruir y Actualizar

### Reconstruir Imagen de la App

```bash
# Reconstruir sin usar caché
docker-compose build --no-cache app

# Reconstruir y levantar
docker-compose up --build -d
```

### Actualizar Código

```bash
# 1. Detener la app
docker-compose stop app

# 2. Actualizar código
git pull

# 3. Reconstruir y levantar
docker-compose up --build -d app
```

## 💾 Gestión de Volúmenes

### Ver Volúmenes

```bash
# Listar volúmenes
docker volume ls | grep baseapi2

# Salida:
# baseapi2_postgres-data
# baseapi2_redis-data
```

### Inspeccionar Volumen

```bash
# Ver detalles del volumen de PostgreSQL
docker volume inspect baseapi2_postgres-data
```

### Backup de Base de Datos

```bash
# Crear backup
docker exec baseapi2-postgres pg_dump -U postgres baseapi2 > backup.sql

# Restaurar backup
cat backup.sql | docker exec -i baseapi2-postgres psql -U postgres -d baseapi2
```

### Limpiar Volúmenes Huérfanos

```bash
# Eliminar volúmenes no usados
docker volume prune
```

## 🔍 Troubleshooting

### Error: "Port already in use"

**Causa:** El puerto ya está ocupado por otro proceso.

**Solución:**
```bash
# Opción 1: Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Usar 8081 en lugar de 8080

# Opción 2: Detener proceso que usa el puerto
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill -9
```

### Error: "Cannot connect to database"

**Causa:** PostgreSQL no está listo o hay problema de red.

**Solución:**
```bash
# Verificar logs de PostgreSQL
docker-compose logs postgres

# Verificar conectividad
docker exec baseapi2-app ping postgres

# Reiniciar servicios en orden
docker-compose down
docker-compose up -d postgres
# Esperar 10 segundos
docker-compose up -d app
```

### Error: "Flyway migration failed"

**Causa:** Migración corrupta o problema en el script SQL.

**Solución:**
```bash
# Ver logs específicos
docker-compose logs app | grep -i flyway

# Reparar Flyway (opción 1)
docker exec baseapi2-app ./gradlew flywayRepair

# Limpiar y re-migrar (opción 2 - solo desarrollo)
docker-compose down -v
docker-compose up -d
```

### Error: "Out of Memory"

**Causa:** La aplicación consume más memoria de la disponible.

**Solución:**
```yaml
# Ajustar en docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m"  # Aumentar memoria
```

### Contenedor se Reinicia Constantemente

```bash
# Ver logs para identificar el problema
docker-compose logs --tail=50 app

# Verificar recursos
docker stats baseapi2-app

# Ejecutar sin restart para ver error
docker-compose up app  # Sin -d
```

## 🌐 Variables de Entorno

### Configuración Completa

```yaml
# docker-compose.yml
app:
  environment:
    # Spring Boot
    SPRING_PROFILES_ACTIVE: local
    
    # Base de Datos
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/baseapi2
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: postgres
    
    # Redis
    SPRING_DATA_REDIS_HOST: redis
    SPRING_DATA_REDIS_PORT: 6379
    
    # Flyway
    SPRING_FLYWAY_ENABLED: true
    
    # JVM
    JAVA_OPTS: "-Xmx512m -Xms256m -XX:+UseG1GC"
    
    # Logging
    LOGGING_LEVEL_ROOT: INFO
    LOGGING_LEVEL_COM_AR_LABORATORY: DEBUG
```

### Archivo .env (Alternativa)

```bash
# Crear archivo .env en la raíz del proyecto
cat > .env << EOF
POSTGRES_PASSWORD=mi_password_seguro
REDIS_PASSWORD=mi_redis_password
APP_PORT=8080
EOF
```

```yaml
# Referenciar en docker-compose.yml
postgres:
  environment:
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

## 🔒 Seguridad

### Mejores Prácticas

1. **No usar credenciales por defecto en producción**
   ```yaml
   environment:
     POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}  # Usar variable de entorno
   ```

2. **Usar redes Docker**
   ```yaml
   services:
     app:
       networks:
         - backend
     postgres:
       networks:
         - backend
   networks:
     backend:
       driver: bridge
   ```

3. **Limitar recursos**
   ```yaml
   app:
     deploy:
       resources:
         limits:
           cpus: '1'
           memory: 1G
   ```

## 📈 Monitoreo

### Recursos

```bash
# Ver uso de CPU, memoria, red
docker stats

# Salida:
# CONTAINER       CPU %    MEM USAGE / LIMIT    NET I/O
# baseapi2-app    2.5%     350MB / 512MB        1.2kB / 850B
# baseapi2-postgres 1.0%   80MB / 2GB          500B / 300B
# baseapi2-redis   0.5%    12MB / 256MB        200B / 100B
```

### Conectarse a Contenedores

```bash
# Shell en la aplicación
docker exec -it baseapi2-app sh

# PostgreSQL CLI
docker exec -it baseapi2-postgres psql -U postgres -d baseapi2

# Redis CLI
docker exec -it baseapi2-redis redis-cli
```

## 🎯 Producción

### Consideraciones

Para producción, considera:

1. **Secrets Management**
   - Usar Docker Secrets o variables de entorno seguras
   - No versionar credenciales

2. **Reverse Proxy**
   - Usar Nginx o Traefik delante de la app
   - Configurar HTTPS con certificados SSL

3. **Monitoreo**
   - Integrar Prometheus + Grafana
   - Logs centralizados (ELK Stack)

4. **Backup Automático**
   - Script de backup diario de PostgreSQL
   - Almacenamiento remoto de backups

5. **Alta Disponibilidad**
   - Réplicas de la aplicación
   - PostgreSQL en modo cluster
   - Redis Sentinel o Cluster

### Ejemplo: Docker Compose para Producción

```yaml
version: '3.8'

services:
  app:
    image: baseapi2:${VERSION:-latest}
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '1'
          memory: 1G
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/baseapi2
      SPRING_DATASOURCE_USERNAME_FILE: /run/secrets/db_user
      SPRING_DATASOURCE_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_user
      - db_password
    networks:
      - backend

secrets:
  db_user:
    external: true
  db_password:
    external: true

networks:
  backend:
    driver: overlay
```

## 📚 Referencias

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot with Docker](https://spring.io/guides/topicals/spring-boot-docker/)

---

**Volver al [README principal](../README.md)**
