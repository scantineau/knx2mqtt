FROM krambox/javashell:latest AS BUILD_IMAGE
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME/build.gradle

RUN gradle jar

FROM openjdk:jre-alpine
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY --from=BUILD_IMAGE /app/build/libs/knx2mqtt.jar /app/
EXPOSE 3671
#java -jar knx2mqtt.jar knx.ip=192.168.1.118 mqtt.server=tcp://192.168.1.13:1883 knx.localip=192.168.1.113
#docker run -it knx2mqtt  
