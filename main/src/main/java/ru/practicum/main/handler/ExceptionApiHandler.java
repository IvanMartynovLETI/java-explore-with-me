package ru.practicum.main.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.exception.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Entity must be unique.", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(EntityDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError entityDoesNotExistException(EntityDoesNotExistException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Entity must exist.", HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(IncorrectOperationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError incorrectOperationException(IncorrectOperationException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Operation incorrect.", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError forbiddenOperationException(ForbiddenOperationException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Forbidden operation.", HttpStatus.FORBIDDEN.getReasonPhrase(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(IncorrectDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError incorrectDataException(IncorrectDataException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Incorrect data.", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError constraintViolationException(ConstraintViolationException e) {
        log.warn(e.toString());

        return new ApiError(e.getMessage(), "Incorrect parameter of request.",
                HttpStatus.BAD_REQUEST.getReasonPhrase(), LocalDateTime.now().format(FORMATTER));
    }
}