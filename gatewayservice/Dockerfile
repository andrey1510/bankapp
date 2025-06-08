FROM amazoncorretto:21 AS build
COPY src /app/src
WORKDIR /app
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-al2023-headful
COPY --from=build /app/build/libs/gatewayservice*.jar /app/gatewayservice.jar
WORKDIR /app
EXPOSE 8889
ENTRYPOINT ["java", "-jar", "/app/gatewayservice.jar"]