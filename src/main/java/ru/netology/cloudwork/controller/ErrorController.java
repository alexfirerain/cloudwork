package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudwork.dto.ErrorDto;

import java.util.concurrent.atomic.AtomicInteger;

@ControllerAdvice
@Slf4j
public class ErrorController {
    private static final AtomicInteger idCount = new AtomicInteger();

    @ExceptionHandler({UsernameNotFoundException.class,
                       BadCredentialsException.class})
    public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("A Bad-Request exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationFailure(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("An Authorization exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleServerError(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("A Serverside Error exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
