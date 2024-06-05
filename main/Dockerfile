# Utiliza una imagen de Maven para construir el JAR
#FROM maven:3.8.4-openjdk-17 AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -Pprod -DskipTests

# Utiliza una imagen de JDK para ejecutar la aplicación
#FROM openjdk:17-slim
#WORKDIR /app
#COPY --from=build /app/target/main-1.0.2-Integraservicio.jar java-app.jar
#ENTRYPOINT ["java", "-jar", "java-app.jar", "--spring.profiles.active=prod"]
# Utiliza una imagen base adecuada
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR al contenedor
COPY target/main-1.0.2-Integraservicio.jar java-app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "/app/java-app.jar", "--spring.profiles.active=dev", "-Dserver.port=$PORT"]

