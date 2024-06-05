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
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/main-1.0.2-Integraservicio.jar java-app.jar

EXPOSE 8080

CMD ["java", "-jar", "/java-app.jar", "--spring.profiles.active=front", "-Dserver.port=$PORT"]
