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

# Create application user for security
RUN addgroup -g 1001 -S wildlife && \
    adduser -S wildlife -u 1001 -G wildlife

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/wildlife-api-*.jar app.jar

# Change ownership to application user
RUN chown -R wildlife:wildlife /app

# Switch to application user
USER wildlife

# Expose port (Railway will set PORT env variable)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# JVM optimization and security settings
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Application configuration
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application (Railway provides PORT env variable)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"] 