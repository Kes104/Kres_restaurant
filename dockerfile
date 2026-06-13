FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew shadowJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/restaurant-0.1-all.jar app.jar
EXPOSE 8040
ENTRYPOINT ["java", "-jar", "app.jar"]