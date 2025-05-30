package org.dariaob.exceptions;

import org.dariaob.exceptions.BadFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadFormatExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Некорректный формат данных";
        BadFormatException exception = new BadFormatException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}

