# ---- Stage 1: build the jar (JDK 25 + your Maven wrapper) ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy the wrapper + pom first so Docker can cache the dependency download
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Then the source, then build (skip tests to keep the deploy build quick)
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: slim runtime image — just the Java runtime + the jar ----
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]