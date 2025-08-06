# Stage 1: Build
FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/target/transactions-routine-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
