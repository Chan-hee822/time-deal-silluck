FROM openjdk:21-jdk-slim
COPY build/libs/discovery-server-0.0.1-SNAPSHOT.jar discovery-server.jar
ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-jar","discovery-server.jar"]