package org.dariaob.controllers.advices;

import org.dariaob.exceptions.BadFormatException;
import org.dariaob.exceptions.DataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Обеспечивает централизованную обработку ошибок и единообразные ответы.
 */
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Обрабатывает ситуации, когда запрашиваемые данные не найдены.
     * @param ex исключение
     * @return ответ с HTTP-статусом NOT_FOUND (404)
     */
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDataNotFound(DataNotFoundException ex) {
        return Map.of(
                "error", "Данные не найдены",
                "message", ex.getMessage(),
                "type", "DATA_MISSING"
        );
    }

    /**
     * Обрабатывает некорректный формат данных.
     * @param ex исключение
     * @return ответ с HTTP-статусом BAD_REQUEST (400)
     */
    @ExceptionHandler(BadFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadFormat(BadFormatException ex) {
        return Map.of(
                "error", "Неверный формат данных",
                "message", ex.getMessage(),
                "type", "INVALID_FORMAT"
        );
    }
}