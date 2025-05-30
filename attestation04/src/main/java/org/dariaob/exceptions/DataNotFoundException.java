package org.dariaob.exceptions;

/**
 * Данные не найдены
 */
public class DataNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Data not found exception.
     *
     * @param message the message
     */
    public DataNotFoundException(String message) {
        super(message);
    }
}
