FROM eclipse-temurin:21.0.6_7-jdk
SHELL ["/bin/bash", "-c"]

ENV TOMCAT_VERSION=11.0.4

ARG TOMCAT_USER
ARG TOMCAT_PASS

RUN useradd -m -U -d /opt/tomcat -s /bin/false tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-11/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz && \
    tar -xf apache-tomcat-$TOMCAT_VERSION.tar.gz -C /opt/tomcat && \
    rm apache-tomcat-$TOMCAT_VERSION.tar.gz && \

RUN ln -s /opt/tomcat/apache-tomcat-$TOMCAT_VERSION /opt/tomcat/latest

COPY CustomersService.war /opt/tomcat/latest/webapps
COPY context.xml /opt/tomcat/latest/webapps/manager/META-INF
COPY server.xml /opt/tomcat/latest/conf

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

RUN chown -R tomcat: /opt/tomcat && \
    chmod -R 755 /opt/tomcat

USER tomcat

HEALTHCHECK --interval=30m --timeout=3s CMD curl --fail http://localhost:8080 || exit 1

ENTRYPOINT ["/entrypoint.sh"]