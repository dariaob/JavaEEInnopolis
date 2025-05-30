package org.dariaob.exceptions;

/**
 * Несовпадение формата передаваемых данных
 */
public class BadFormatException extends RuntimeException {

    /**
     * Instantiates a new Bad format exception.
     *
     * @param msg the msg
     */
    public BadFormatException(String msg) {
        super(msg);
    }
}
