FROM openjdk:21-jdk-slim
COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-jar","api-gateway.jar"]