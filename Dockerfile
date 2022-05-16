FROM fabric8/java-centos-openjdk8-jdk

MAINTAINER Thomas Philipona <philipona@puzzle.ch>

EXPOSE 8080 9090

RUN mkdir -p /tmp/src/
COPY src /tmp/src/src
COPY gradle /tmp/src/gradle
COPY build.gradle gradlew settings.gradle /tmp/src/

RUN cd /tmp/src && sh gradlew build --debug -Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx1g -XX:MaxMetaspaceSize=256m"

RUN cp -a /tmp/src/build/libs/*.jar /deployments/marina-backend.jar