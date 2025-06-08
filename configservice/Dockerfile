FROM amazoncorretto:21 AS build
COPY src /app/src
WORKDIR /app
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-al2023-headful
COPY --from=build /app/build/libs/configservice*.jar /app/configservice.jar
WORKDIR /app
EXPOSE 8884
ENTRYPOINT ["java", "-jar", "/app/configservice.jar"]