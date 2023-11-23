FROM tomcat:9.0.80-jdk17

RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/examples
ADD /target/shop.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]


