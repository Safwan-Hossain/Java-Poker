# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy only what's needed to download dependencies
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the actual source code and build the JAR
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# App listens on port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
