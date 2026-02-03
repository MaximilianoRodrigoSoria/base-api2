# ========================================
# FASE 1: Construcción (Build Stage)
# ========================================
FROM gradle:8.14-jdk17-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Etiqueta personalizada: Inicio de fase de construcción
RUN echo "=========================================" && \
    echo "🏗️  FASE 1: CONSTRUCCIÓN (BUILD STAGE)" && \
    echo "📦 Preparando compilación de Base API 2" && \
    echo "========================================="

# Copiar archivos de configuración de Gradle primero (para cache de dependencias)
COPY build.gradle settings.gradle ./

# Etiqueta personalizada: Descarga de dependencias
RUN echo "📥 Descargando dependencias de Gradle..." && \
    gradle dependencies --no-daemon || true && \
    echo "✅ Dependencias descargadas correctamente"

# Copiar el resto de los archivos necesarios
COPY src ./src

# Etiqueta personalizada: Compilación (spotlessApply se ejecuta automáticamente)
RUN echo "🔨 Compilando aplicación Spring Boot..." && \
    echo "✨ (Spotless formateará el código automáticamente)" && \
    gradle clean build -x test --no-daemon && \
    echo "✅ Compilación exitosa - JAR generado"

# ========================================
# FASE 2: Ejecución (Runtime Stage)
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Metadata de la imagen
LABEL maintainer="base-api2"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Base API 2 - Spring Boot Application"

# Etiqueta personalizada: Inicio de fase de runtime
RUN echo "=========================================" && \
    echo "🚀 FASE 2: EJECUCIÓN (RUNTIME STAGE)" && \
    echo "📦 Preparando imagen final optimizada" && \
    echo "========================================="

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la fase de construcción
COPY --from=builder /app/build/libs/*.jar app.jar

# Etiqueta personalizada: Tamaño del JAR
RUN echo "📊 Información del JAR:" && \
    ls -lh app.jar && \
    echo "✅ JAR copiado exitosamente"

# Crear usuario no-root para mayor seguridad
RUN echo "🔒 Configurando seguridad..." && \
    addgroup -S spring && adduser -S spring -G spring && \
    echo "✅ Usuario no-root creado: spring"

USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE="local"

# Etiqueta personalizada final
RUN echo "=========================================" && \
    echo "✅ Imagen final lista para ejecutar" && \
    echo "🌐 Puerto expuesto: 8080" && \
    echo "💾 Memoria JVM: 256MB-512MB" && \
    echo "========================================="

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
