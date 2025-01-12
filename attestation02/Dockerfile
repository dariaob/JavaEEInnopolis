FROM openjdk:23
COPY target/attestation02-1.0-SNAPSHOT.jar app.jar
COPY application.properties application.properties
ENTRYPOINT ["java", "-jar", "app.jar"]
