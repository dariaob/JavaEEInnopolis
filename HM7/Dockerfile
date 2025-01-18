FROM openjdk:21-jdk-slim

# Установите рабочую директорию
WORKDIR /HM7

# Скопируйте JAR-файл в контейнер
COPY target/HM7-1.0-SNAPSHOT-jar-with-dependencies.jar hm7.jar
RUN ls -al /HM7
# Запустите приложение
ENTRYPOINT ["java", "-jar", "hm7.jar"]
