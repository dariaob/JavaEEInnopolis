FROM openjdk:21-jdk-slim

# Установите рабочую директорию
WORKDIR /app

# Скопируйте JAR-файл в контейнер
COPY target/Homework5-1.0-SNAPSHOT.jar hm5.jar

COPY src/main/resources/input.txt app/input.txt
COPY src/main/resources/inputSet.txt app/inputSet.txt
# Запустите приложение
ENTRYPOINT ["java", "-jar", "hm5.jar"]
