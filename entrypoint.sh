#!/bin/bash

# Fail early if secrets are not set
if [[ -z "$TOMCAT_USER" || -z "$TOMCAT_PASS" ]]; then
  echo "TOMCAT_USER and TOMCAT_PASS must be set as environment variables."
  exit 1
fi

# Generate tomcat-users.xml at runtime
cat <<EOF > /opt/tomcat/latest/conf/tomcat-users.xml
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
              version="1.0">
  <role rolename="manager-gui"/>
  <user username="${TOMCAT_USER}" password="${TOMCAT_PASS}" roles="manager-gui"/>
</tomcat-users>
EOF

# Launch Tomcat
exec /opt/tomcat/latest/bin/catalina.sh run
