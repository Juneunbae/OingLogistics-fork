FROM gradle:8.5-jdk17 AS build

WORKDIR /build

COPY --chown=gradle:gradle . .

RUN gradle :delivery-service:bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /build/delivery-service/build/libs/*.jar app.jar
COPY --from=build /build/delivery-service/build/libs /app/libs

COPY common/build.gradle /build/common/
COPY common/src /build/common/src

ENTRYPOINT ["java", "-jar", "app.jar"]