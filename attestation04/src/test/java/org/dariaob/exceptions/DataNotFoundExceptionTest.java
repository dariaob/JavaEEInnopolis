package org.dariaob.exceptions;

import org.dariaob.exceptions.DataNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String errorMessage = "Данные не найдены";
        DataNotFoundException exception = new DataNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}
