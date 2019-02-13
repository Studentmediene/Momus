FROM maven:3.5-jdk-8-alpine as cache

WORKDIR /

COPY pom.xml pom.xml

# Download dependencies
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline

# Add certificate needed for talking to LDAP
COPY certificate.crt .
RUN keytool -importcert -file certificate.crt -alias momus -keystore /etc/ssl/certs/java/cacerts -storepass changeit -noprompt

FROM cache as backend

COPY src/main/filters src/main/filters

# Add already built frontend resources
COPY webapp/dist webapp/dist
COPY webapp-beta/dist webapp-beta/dist

# Jetty needs this
COPY src/main/webapp/WEB-INF src/main/webapp/WEB-INF

# Add source
COPY src/main/resources src/main/resources
COPY src/test src/test
COPY src/main/java src/main/java

RUN mvn install -DskipTests

EXPOSE 8080
ENTRYPOINT ["mvn"]
CMD ["jetty:run"]