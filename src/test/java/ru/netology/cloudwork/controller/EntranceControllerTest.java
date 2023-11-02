package ru.netology.cloudwork.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.netology.cloudwork.TestData.*;

class EntranceControllerTest {

    private EntranceController entranceController;
    private final CloudworkAuthorizationService cloudworkAuthorizationService = mock(CloudworkAuthorizationService.class);

    private final LoginResponse TOKEN_RESPONSE = new LoginResponse("new token");

    @BeforeEach
    void setup() {
        entranceController = new EntranceController(cloudworkAuthorizationService);
        when(cloudworkAuthorizationService.initializeSession(LOGIN_REQUEST))
                .thenReturn(new LoginResponse("new token"));
        when(cloudworkAuthorizationService.initializeSession(LOGIN_REQUEST_BAD_PASSWORD))
                .thenThrow(new BadCredentialsException("Неверный пароль."));
        when(cloudworkAuthorizationService.initializeSession(LOGIN_REQUEST_BAD_LOGIN))
                .thenThrow(new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован."));

    }
    @Test
    void login_correct() {
        ResponseEntity<LoginResponse> expectedResponse = ResponseEntity.ok(TOKEN_RESPONSE);

        Assertions.assertEquals(expectedResponse, entranceController.login(LOGIN_REQUEST));
    }

    @Test
    void login_incorrect() {
        Assertions.assertThrows(BadCredentialsException.class,
                () -> entranceController.login(LOGIN_REQUEST_BAD_PASSWORD));
    }

    @Test
    void wrong_username_denial() {
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> entranceController.login(LOGIN_REQUEST_BAD_LOGIN));
    }



}