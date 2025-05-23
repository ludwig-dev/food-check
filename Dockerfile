# Use Java 21 runtime
FROM openjdk:21-jdk-slim

# Set a temporary volume
VOLUME /tmp

# Copy the built JAR file into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]
