FROM openjdk:21-jdk-slim

# Установите рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/HM4-1.0-SNAPSHOT.jar  hm4.jar

COPY src/main/resources/input.txt app/input.txt
# Запустите приложение
ENTRYPOINT ["java", "-jar", "hm4.jar"]
