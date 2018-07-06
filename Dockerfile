FROM openjdk:8-jdk-alpine
VOLUME /tmp

ADD ./target/a2si-dos-wrapper-0.0.7-SNAPSHOT.jar dos-wrapper.jar
ADD ./keystore.jks keystore.jks

# Expose 7030, the default port used for Dos Wrapper
EXPOSE 7030
ENV JAVA_OPTS=""
RUN ["apk", "update"]
RUN ["apk", "add", "tzdata"]
RUN ["ln", "-f", "-s", "/usr/share/zoneinfo/Europe/London", "/etc/localtime"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar dos-wrapper.jar" ]