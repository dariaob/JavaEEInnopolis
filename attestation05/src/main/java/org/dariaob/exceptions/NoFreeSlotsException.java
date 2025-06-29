package org.dariaob.exceptions;

public class NoFreeSlotsException extends RuntimeException {
    public NoFreeSlotsException(String message) {
        super(message);
    }
}
