# Configuración de local
#FROM openjdk:21
#COPY target/main-1.0.2-Integraservicio.jar java-app.jar
#ENTRYPOINT ["java", "-jar", "java-app.jar", "--spring.profiles.active=prod"]
#FROM openjdk:21
#COPY target/*.jar java-app.jar
#CMD java -jar java-app.jar --spring.profiles.active=prod -Dserver.port=$PORT
# Usar una imagen base que incluya Maven para construir el proyecto
FROM maven:3.9.6-openjdk-21 as builder

# Copiar el código fuente al contenedor
COPY . /app
WORKDIR /app

# Construir el proyecto y el archivo .jar
RUN mvn clean package

# Usar openjdk para ejecutar la aplicación
FROM openjdk:11-jre-slim
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
