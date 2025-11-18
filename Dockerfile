# Multi-stage Dockerfile
FROM maven:3.9.4-eclipse-temurin-17 as build
WORKDIR /workspace/app
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/app/target/untitled-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

