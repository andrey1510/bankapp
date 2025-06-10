FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar blockerservice.jar
ENTRYPOINT ["java", "-jar", "blockerservice.jar"]