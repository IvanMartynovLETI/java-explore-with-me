package ru.practicum.main.exception;

public class IncorrectOperationException extends RuntimeException {
    public IncorrectOperationException(String s) {
        super(s);
    }
}