package org.dariaob.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Содержит методы для форматирования дат в едином стиле по всему приложению.
 */
public class DateUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Преобразует LocalDateTime в строку формата "dd.MM.yyyy HH:mm".
     *
     * @param dateTime объект LocalDateTime для преобразования
     * @return отформатированная строка или null, если входной объект null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * Преобразует LocalDateTime в строку формата "HH:mm" (только время).
     *
     * @param dateTime объект LocalDateTime для преобразования
     * @return отформатированная строка или null, если входной объект null
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : null;
    }
}
