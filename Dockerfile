# ----- Builder -----
FROM openjdk:17.0.1-jdk-slim AS builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# ----- Runtime -----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /application
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./
EXPOSE 8080
EXPOSE 5005
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
