# Multi-stage build for optimized production image
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY src/ ./src/

# Build the application
RUN mvn clean package -DskipTests

# Production image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/wildlife-api-*.jar app.jar

# Expose port (Railway will set PORT env variable)
EXPOSE 3001

# Application configuration
ENV SPRING_PROFILES_ACTIVE=dev

# Run the application (Railway provides PORT env variable)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-3001} -jar app.jar"]