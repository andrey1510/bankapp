FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar frontservice.jar
ENTRYPOINT ["java", "-jar", "frontservice.jar"]