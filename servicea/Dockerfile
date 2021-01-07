FROM openjdk:8-jdk-alpine
ENV PORT 8081

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/app.jar"]