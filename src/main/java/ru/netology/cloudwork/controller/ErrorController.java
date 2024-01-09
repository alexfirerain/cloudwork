package ru.netology.cloudwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudwork.dto.ErrorDto;

/**
 * This wise ErrorController is designed to help all CloudWork beans cope with troubles.
 * It will intercept and handle every exceptional event in the CloudWork.
 */
@ControllerAdvice
@Slf4j
public class ErrorController {

    /**
     * Handles situations when there's a bad request, username not found
     * or password not matched.
     * @param exception an exception being caught.
     * @return a 400 http-response with error's description and number.
     */
    @ExceptionHandler({
            UsernameNotFoundException.class,
                       BadCredentialsException.class,
                       ServletRequestBindingException.class })
    public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
        ErrorDto response = new ErrorDto(exception);
        log.warn("A Bad-Request exception happened: {}", response.message());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles situations when there's a problem with
     * user identity, credentials, authorization and so on.
     * @param exception an exception being caught.
     * @return a 401 http-response with error's description and number.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationFailure(RuntimeException exception) {
        ErrorDto response = new ErrorDto(exception);
        log.warn("An Authorization exception happened: {}", response.message());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    /**
     * Handles situations when any other errors in da app occur.
     * @param exception an exception being caught.
     * @return a 500 http-response with error's description and number.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleServerError(Exception exception) {
        ErrorDto response = new ErrorDto(exception);
        log.warn("A Serverside Error '{}' happened: {}", exception.getClass().getSimpleName(), response.message());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
