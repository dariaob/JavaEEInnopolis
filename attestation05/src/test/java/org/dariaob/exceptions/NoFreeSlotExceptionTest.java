package org.dariaob.exceptions;

import org.dariaob.exceptions.NoFreeSlotsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoFreeSlotExceptionTest {
        @Test
        void testExceptionMessage() {
            String errorMessage = "Нет свободных слотов в это время";
            NoFreeSlotsException exception = new NoFreeSlotsException(errorMessage);

            assertEquals(errorMessage, exception.getMessage());
        }

}
