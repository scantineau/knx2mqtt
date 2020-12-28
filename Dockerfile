FROM mrfroop/openjdk-alpine-gradle AS BUILD_IMAGE

#build knx2mqtt.jar
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME/build.gradle
RUN gradle jar

#Build run image
FROM openjdk:jre-alpine
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY --from=BUILD_IMAGE /app/build/libs/knx2mqtt.jar /app/
VOLUME ["/data"]
CMD java -jar knx2mqtt.jar