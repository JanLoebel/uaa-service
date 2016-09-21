FROM frolvlad/alpine-oraclejdk8:slim

ENV SLEEP 0

ADD *.jar /app.jar

RUN sh -c 'touch /app.jar'
VOLUME /tmp
EXPOSE 8080
CMD echo "Application start in ${SLEEP} seconds..." && \
    sleep ${SLEEP} && \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
