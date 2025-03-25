FROM gradle:8.5-jdk17 AS build

WORKDIR /build

COPY --chown=gradle:gradle . .

RUN gradle :company-service:bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /build/company-service/build/libs/*.jar app.jar
COPY --from=build /build/company-service/build/libs /app/libs

COPY common/build.gradle /build/common/
COPY common/src /build/common/src

ENTRYPOINT ["java", "-jar", "app.jar"]