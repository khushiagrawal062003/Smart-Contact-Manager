# ==========================================
# Stage 1: Build Stage using Maven and JDK 17
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and compile the jar file
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 2: Runtime Stage (Lightweight JRE)
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default internal port (Render overrides this via the PORT environment variable)
EXPOSE 8090

# Run the jar, dynamically binding to Render's allocated PORT (fallback to 8090 if not set)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8090}"]
