package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

/**
 * A controller for user login and logout.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class EntranceController {

    /**
     * A service for managing users sessions.
     */
    private final CloudworkAuthorizationService authorizationService;

    /**
     * This controller method handles a POST request to "/login" endpoint.
     * It receives a validated {@link LoginRequest} object as the request body
     * and calls the "{@link CloudworkAuthorizationService#initializeSession(LoginRequest) initializeSession()}" method
     * of the {@link #authorizationService} to generate a LoginResponse object
     * containing a token offer.
     * @param loginRequest a {@link LoginRequest JSON-formed request} to the application containing
     *                     login and password pair.
     * @return a ResponseEntity object with the {@link LoginResponse} object as the response body.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.debug("Request to log in as '{}'", loginRequest.login());
        LoginResponse tokenOffer = authorizationService.initializeSession(loginRequest);
        log.trace("Controller returns {}", tokenOffer);
        return ResponseEntity.ok(tokenOffer);
    }

    /**
     * A handler to handle a redirect request on logout,
     * "/logout" endpoint having been treated by a {@link ru.netology.cloudwork.filter.CloudworkLogoutHandler Spring Boot logout handler}.
     * @return  OK response entity.
     */
    @GetMapping("/login")
    public ResponseEntity<?> logoutRedirection() {
        log.debug("a posterior GET request to login endpoint");
        return ResponseEntity.ok().build();
    }

}
