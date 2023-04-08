package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudwork.dto.ErrorDto;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A general controller designed to intercept and handle
 * every exceptional events in the CloudWork.
 */
@ControllerAdvice
@Slf4j
public class ErrorController {
    /**
     * An all-through numerator of errors in da app.
     * The generated sequence does not persist and starts over
     * with every application run.
     */
    private static final AtomicInteger idCount = new AtomicInteger();


    /**
     * Handles situations when there's a bad request, username not found
     * or password not matched.
     * @param exception an exception being caught.
     * @return a 400 http-response with error's description and number.
     */
    @ExceptionHandler({UsernameNotFoundException.class,
                       BadCredentialsException.class,
                       ServletRequestBindingException.class })
    public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("A Bad-Request exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles situations when there's a problem with
     * user identity, credentials, authorization and so on.
     * @param exception an exception being caught.
     * @return a 401 http-response with error's description and number.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationFailure(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("An Authorization exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles situations when any other errors in da app occur.
     * @param exception an exception being caught.
     * @return a 500 http-response with error's description and number.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleServerError(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("A Serverside Error exception happened: {}", message);
        return new ResponseEntity<>(
                new ErrorDto(message, idCount.getAndIncrement()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
