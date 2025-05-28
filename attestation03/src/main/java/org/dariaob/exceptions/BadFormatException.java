package org.dariaob.exceptions;

/**
 * Несовпадение формата передаваемых данных
 */
public class BadFormatException extends RuntimeException {

    public BadFormatException(String msg) {
        super(msg);
    }
}
