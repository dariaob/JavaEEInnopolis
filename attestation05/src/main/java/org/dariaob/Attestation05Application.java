package org.dariaob;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Класс, запускающий приложение и поднятие Spring - контекста
 */
@SpringBootApplication
public class Attestation05Application {

    /**
     * Процедура запуска Spring - приложения
     *
     * @param args модификаторы способа запуска
     */
    public static void main(String[] args) {
        SpringApplication.run(Attestation05Application.class, args);
    }

}