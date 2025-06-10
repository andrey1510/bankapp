FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar exchangeservice.jar
ENTRYPOINT ["java", "-jar", "exchangeservice.jar"]