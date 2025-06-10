FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar cashservice.jar
ENTRYPOINT ["java", "-jar", "cashservice.jar"]