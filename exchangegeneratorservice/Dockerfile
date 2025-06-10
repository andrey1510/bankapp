FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar exchangegeneratorservice.jar
ENTRYPOINT ["java", "-jar", "exchangegeneratorservice.jar"]