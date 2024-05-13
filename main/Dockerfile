# Configuración de local
#FROM openjdk:21
#COPY target/main-1.0.2-Integraservicio.jar java-app.jar
#ENTRYPOINT ["java", "-jar", "java-app.jar", "--spring.profiles.active=prod"]
FROM openjdk:21
COPY target/*.jar java-app.jar
CMD java -jar java-app.jar --spring.profiles.active=prod -Dserver.port=$PORT
