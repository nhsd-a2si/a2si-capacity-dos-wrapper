FROM openjdk:8-jdk-alpine
VOLUME /tmp

ADD ./target/dos-wrapper-0.0.1-SNAPSHOT.jar dos-wrapper.jar

# Expose 7030, the default port used for Dos Wrapper
EXPOSE 7030
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar dos-wrapper.jar" ]