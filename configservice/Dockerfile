FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar configservice.jar
ENTRYPOINT ["java", "-jar", "configservice.jar"]