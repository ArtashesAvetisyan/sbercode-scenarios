FROM gradle:jdk11 AS build-env
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM gcr.io/distroless/java:11
COPY --from=build-env /home/gradle/src/build/libs/*.jar /app/app.jar
WORKDIR /app
CMD ["app.jar"]