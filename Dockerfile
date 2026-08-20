
FROM gradle:9.0-jdk21 AS build
WORKDIR /project

COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle bootJar --no-daemon -x test


FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /project/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]