FROM eclipse-temurin:17.0.6_10-jdk
SHELL ["/bin/bash", "-c"]

ENV TOMCAT_VERSION=10.1.7

RUN useradd -m -U -d /opt/tomcat -s /bin/false tomcat
RUN wget https://downloads.apache.org/tomcat/tomcat-10/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz && \
    tar -xf apache-tomcat-$TOMCAT_VERSION.tar.gz -C /opt/tomcat && \
    rm apache-tomcat-$TOMCAT_VERSION.tar.gz && \
    chown -R tomcat: /opt/tomcat

COPY CustomersService.war /opt/tomcat/apache-tomcat-$TOMCAT_VERSION/webapps
COPY tomcat-users.xml /opt/tomcat/apache-tomcat-$TOMCAT_VERSION/conf
COPY context.xml /opt/tomcat/apache-tomcat-$TOMCAT_VERSION/webapps/manager/META-INF
COPY server.xml /opt/tomcat/apache-tomcat-$TOMCAT_VERSION/conf

RUN echo "0" > healthy

CMD ["bash", "-c", "/opt/tomcat/apache-tomcat-$TOMCAT_VERSION/bin/catalina.sh run"]