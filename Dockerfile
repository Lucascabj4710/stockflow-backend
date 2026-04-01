# Dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Jar generado por Maven en target/
ARG JAR_FILE=target/stockflow-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Comando para iniciar la app
ENTRYPOINT ["java","-jar","/app.jar"]