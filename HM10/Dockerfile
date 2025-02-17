FROM openjdk:21-jdk-slim

# Установите рабочую директорию
WORKDIR /app

# Скопируйте JAR-файл в контейнер
COPY target/HM10-1.0-SNAPSHOT.jar hm10.jar

COPY src/main/resources/input.txt app/input.txt
# Запустите приложение
ENTRYPOINT ["java", "-jar", "hm10.jar"]
