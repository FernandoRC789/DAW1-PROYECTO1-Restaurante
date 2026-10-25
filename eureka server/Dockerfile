# --- ETAPA 1: Compilar el proyecto con Maven dentro de Docker ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Compila el proyecto saltándose las pruebas para acelerar el proceso
RUN mvn clean package -DskipTests

# --- ETAPA 2: Crear la imagen ligera para ejecutar la aplicación ---
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
# Copia el JAR generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]