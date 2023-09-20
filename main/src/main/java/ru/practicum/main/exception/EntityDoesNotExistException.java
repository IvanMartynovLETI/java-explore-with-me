package ru.practicum.main.exception;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String s) {
        super(s);
    }
}