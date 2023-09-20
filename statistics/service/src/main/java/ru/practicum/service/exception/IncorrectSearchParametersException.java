package ru.practicum.service.exception;

public class IncorrectSearchParametersException extends RuntimeException {
    public IncorrectSearchParametersException(String s) {
        super(s);
    }
}
