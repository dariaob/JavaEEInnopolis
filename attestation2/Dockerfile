FROM openjdk:21-jdk-slim

# Установите рабочую директорию
WORKDIR /app

# Скопируйте JAR-файл в контейнер
COPY target/attestation2-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Запустите приложение
ENTRYPOINT ["java", "-jar", "app.jar"]

