package ru.practicum.main.exception;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String s) {
        super(s);
    }
}
