FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# 전체 프로젝트 복사
COPY . .

# 특정 모듈만 빌드
RUN ./gradlew :delivery-service:build -x test

# 실행 스테이지
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/delivery-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]