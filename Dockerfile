FROM adoptopenjdk/openjdk8:jdk8u202-b08-alpine-slim

ENV APP_DIR="/opt/task/app"
ENV APP_JAR="task-*.jar"

RUN mkdir -p $APP_DIR

COPY target/$APP_JAR $APP_DIR/

RUN printf "#!/bin/sh \n java -Dtask.docker.enabled=true -Dtask.mongo.uri=mongodb://task-mongo:27017/applause -jar $APP_DIR/$APP_JAR" >> /entrypoint.sh && chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]