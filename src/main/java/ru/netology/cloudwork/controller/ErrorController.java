package ru.netology.cloudwork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cloudwork.dto.ErrorDto;

import java.util.concurrent.atomic.AtomicInteger;

@ControllerAdvice
public class ErrorController {

    private static final AtomicInteger idCount = new AtomicInteger();


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
        return new ResponseEntity<>(
                new ErrorDto(exception.getLocalizedMessage(), idCount.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

}
