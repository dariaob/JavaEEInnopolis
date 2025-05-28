package org.dariaob.exceptions;

/**
 * Данные не найдены
 */
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
