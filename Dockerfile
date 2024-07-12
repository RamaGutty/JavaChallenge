# Base específica de OpenJDK 17.0.6
FROM openjdk:17-jdk-slim

# Directorio para la aplicación
WORKDIR /app

# Archivo JAR de la aplicación al contenedor
COPY target/java-challenge.jar /app/java-challenge.jar

# Puerto expuesto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/java-challenge.jar"]
