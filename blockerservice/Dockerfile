FROM amazoncorretto:21 AS build
COPY src /app/src
WORKDIR /app
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-al2023-headful
COPY --from=build /app/build/libs/blockerservice*.jar /app/blockerservice.jar
WORKDIR /app
EXPOSE 8882
ENTRYPOINT ["java", "-jar", "/app/blockerservice.jar"]