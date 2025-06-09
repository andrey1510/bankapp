FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar gatewayservice.jar
ENTRYPOINT ["java", "-jar", "gatewayservice.jar"]