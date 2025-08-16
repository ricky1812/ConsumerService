FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar
COPY wait-for-it.sh /app/wait-for-it.sh

RUN chmod +x /app/wait-for-it.sh

ENTRYPOINT ["/app/wait-for-it.sh", "postgres:5432", "--", "/app/wait-for-it.sh", "kafka:9092", "--", "java", "-jar", "app.jar"]
