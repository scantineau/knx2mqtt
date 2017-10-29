FROM openjdk:alpine AS BUILD_IMAGE

#install gradle
WORKDIR /opt/
ADD https://services.gradle.org/distributions/gradle-3.4.1-bin.zip .
RUN unzip gradle-3.4.1-bin.zip
ENV PATH="${PATH}:/opt/gradle-3.4.1/bin"

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
RUN java -jar knx2mqtt.jar knx.ip=192.168.1.118 mqtt.server=tcp://192.168.1.13:1883  knx.nat=NAT

#java -jar knx2mqtt.jar knx.ip=192.168.1.118 mqtt.server=tcp://192.168.1.13:1883  knx.nat=NAT
#docker run -it knx2mqtt  
#docker build -t knx2mqtt .