# syntax = docker/dockerfile:1.2

FROM adoptopenjdk/openjdk11:latest as build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY settings.xml .
COPY src src

RUN chmod +x /mvnw
RUN --mount=type=secret,id=maven_repo_password,dst=/etc/secrets/maven_repo_password  \
    export MAVEN_REPO_PASSWORD=$(cat /etc/secrets/maven_repo_password)  &&  \
    ./mvnw install -s settings.xml -DskipTests

FROM adoptopenjdk/openjdk11:alpine-slim

VOLUME /tmp

COPY --from=build target/*.jar app.jar

EXPOSE 80

USER root
ENTRYPOINT ["java", "-jar", "/app.jar"]