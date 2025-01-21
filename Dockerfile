# Stage 1: builder
From openjdk:21-jdk-slim AS builder

RUN apt-get update && apt-get install -y dos2unix findutils

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN mkdir -p /app/src/main/resources

COPY src/main/resources/application.yml /app/application.yml

RUN chmod +x ./gradlew
RUN dos2unix ./gradlew

RUN ./gradlew dependencies || true

RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]