FROM openjdk:21
COPY db-api.jar app.jar
COPY application.properties application.properties
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080