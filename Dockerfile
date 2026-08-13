# Use official OpenJDK image as base
FROM eclipse-temurin:17-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy wrapper and pom files first for dependency caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (this caches them if pom.xml hasn't changed)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application JAR file
RUN ./mvnw clean package -DskipTests

# Run the compiled Spring Boot jar
CMD ["java", "-jar", "target/cyphercart-backend-0.0.1-SNAPSHOT.jar"]