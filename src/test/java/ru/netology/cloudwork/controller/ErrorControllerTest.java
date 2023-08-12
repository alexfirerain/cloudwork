package ru.netology.cloudwork.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.netology.cloudwork.dto.ErrorDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorControllerTest {

    @Mock
    private RuntimeException exception;

    @InjectMocks
    private ErrorController errorController;

    @Test
    public void testHandleBadRequest() {
        // Test case 1: UsernameNotFoundException
        when(exception.getLocalizedMessage()).thenReturn("User not found");
        ResponseEntity<ErrorDto> response = errorController.handleBadRequest(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", Objects.requireNonNull(response.getBody()).getMessage());

        // Test case 2: BadCredentialsException
        when(exception.getLocalizedMessage()).thenReturn("Invalid credentials");
        response = errorController.handleBadRequest(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", Objects.requireNonNull(response.getBody()).getMessage());

        // Test case 3: ServletRequestBindingException
        when(exception.getLocalizedMessage()).thenReturn("Missing required parameter");
        response = errorController.handleBadRequest(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required parameter", Objects.requireNonNull(response.getBody()).getMessage());
    }
}

