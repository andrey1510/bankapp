FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar accountservice.jar
ENTRYPOINT ["java", "-jar", "accountservice.jar"]