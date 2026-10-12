# Tests run in CI (they need Docker for Testcontainers), so the image build skips them.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 jobtrack
COPY --from=build /app/target/jobtrack-*.jar app.jar
USER jobtrack
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
