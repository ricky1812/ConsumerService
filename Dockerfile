FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar


ENTRYPOINT ["kafka:9092", "--", "java", "-jar", "app.jar"]
