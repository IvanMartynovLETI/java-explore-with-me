package ru.practicum.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.service.exception.ErrorResponse;
import ru.practicum.service.exception.IncorrectSearchParametersException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @org.springframework.web.bind.annotation.ExceptionHandler(IncorrectSearchParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectSearchParametersException(IncorrectSearchParametersException e) {
        log.warn(e.toString());

        return new ErrorResponse(e.getMessage(), "Time parameters must be non-null.", HttpStatus.BAD_REQUEST
                .getReasonPhrase(), LocalDateTime.now().format(FORMATTER));
    }
}
