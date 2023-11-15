FROM maven:3.9.4-eclipse-temurin-11-focal
WORKDIR /app
COPY . /app
COPY target/shop.war /usr/local/tomcat/webapps/shop.war
COPY src/main/resources/hibernate-docker.cfg.xml /app/src/main/resources/hibernate-dev.cfg.xml
CMD mvn -f /app/pom.xml package
# Use Heroku's OpenJDK image as the base image

