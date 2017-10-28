FROM krambox/baseshell:latest AS BUILD_IMAGE
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME/build.gradle

RUN gradle jar
