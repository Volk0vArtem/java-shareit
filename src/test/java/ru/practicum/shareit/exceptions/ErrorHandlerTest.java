package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("message");
        Map<String, String> message = errorHandler.handleNotFoundException(exception);
        assertEquals("message", message.get("error"));
    }

    @Test
    void testHandleValidationException() {
        ValidationException exception = new ValidationException("message");
        Map<String, String> message = errorHandler.handleValidationException(exception);
        assertEquals("message", message.get("error"));
    }

    @Test
    void testHandleNumberFormatException() {
        NumberFormatException exception = new NumberFormatException("message");
        Map<String, String> message = errorHandler.handleNumberFormatException(exception);
        assertEquals("message", message.get("error"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("message");
        Map<String, String> message = errorHandler.handleIllegalArgumentException(exception);
        assertEquals("message", message.get("error"));
    }

    @Test
    void testHandleConflictException() {
        ConflictException exception = new ConflictException("message");
        Map<String, String> message = errorHandler.handleConflictException(exception);
        assertEquals("message", message.get("error"));
    }

    @Test
    void testHandleForbiddenException() {
        ForbiddenException exception = new ForbiddenException("message");
        Map<String, String> message = errorHandler.handleForbiddenException(exception);
        assertEquals("message", message.get("error"));
    }
}