FROM openjdk:21
MAINTAINER heathgordon
COPY target/gs-maven-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]