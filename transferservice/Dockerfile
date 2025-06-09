FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar transferservice.jar
ENTRYPOINT ["java", "-jar", "transferservice.jar"]