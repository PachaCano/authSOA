FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} api-auth.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/api-auth.jar"]
