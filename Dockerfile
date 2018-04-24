FROM centos/s2i-base-centos7

# Install Java
RUN INSTALL_PKGS="tar unzip bc which lsof java-1.8.0-openjdk java-1.8.0-openjdk-devel" && \
    yum install -y $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all -y && \
    mkdir -p /opt/s2i/destination

EXPOSE 8080
USER 1001

COPY src /opt/app-root/src/src
COPY gradle /opt/app-root/src/gradle
COPY build.gradle gradlew /opt/app-root/src/
# build the application from source
RUN sh /opt/app-root/src/gradlew build

USER 1001

RUN mv /opt/app-root/src/build/libs/*.jar /opt/app-root/marina-backend.jar 

CMD java -Xmx1024m -Xss1024k -jar /opt/app-root/marina-backend.jar

