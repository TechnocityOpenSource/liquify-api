FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim

ARG JAVA_OPTS=-Dmicronaut.environments=dev
ENV JAVA_OPTS=$JAVA_OPTS

COPY target/liquify-api*.jar liquify-api.jar

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar liquify-api.jar

EXPOSE 80
