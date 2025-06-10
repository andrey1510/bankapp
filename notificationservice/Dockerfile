FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar notificationservice.jar
ENTRYPOINT ["java", "-jar", "notificationservice.jar"]