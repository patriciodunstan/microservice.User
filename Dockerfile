# === Build Stage ===
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar primero el POM para aprovechar cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar el proyecto y generar el .jar (sin test, eso lo hace CI)
RUN mvn clean package -DskipTests

# === Runtime Stage ===
FROM eclipse-temurin:17-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Instalar utilidades necesarias
RUN apk add --no-cache dumb-init curl

# Directorio de trabajo
WORKDIR /app

# Copiar el .jar desde el build stage
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

# Cambiar a usuario seguro
USER appuser

# Exponer el puerto que usa la app
EXPOSE 8080

# Agregar metadata de imagen
LABEL org.opencontainers.image.title="microservice-user" \
      org.opencontainers.image.version="0.0.1-SNAPSHOT" \
      org.opencontainers.image.authors="Patricio Dunstan" \
      org.opencontainers.image.description="Java microservice backend"

# Healthcheck dentro del contenedor
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/auth/health || exit 1

# Ejecutar la app usando dumb-init para manejo correcto de señales
ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]
