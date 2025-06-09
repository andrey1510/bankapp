FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/*.jar eurekaservice.jar
ENTRYPOINT ["java", "-jar", "eurekaservice.jar"]