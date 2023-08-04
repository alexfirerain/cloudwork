package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.service.UserService;

import java.security.Principal;

/**
 * A controller for user login and logout.
 */
@RestController
@Slf4j
public class EntranceController {

    private final UserService userService;

    /**
     * Creates a new {@link EntranceController} linked with an instance of
     * @param userService a {@link UserService}.
     */
    public EntranceController(UserService userService) {
        this.userService = userService;
    }

    /**
     * This controller method handles a POST request to "/login" endpoint.
     * It receives a validated {@link LoginRequest} object as the request body
     * and calls the "{@link UserService#initializeSession(LoginRequest) initializeSession()}" method
     * of the {@link #userService} to generate a LoginResponse object
     * containing a token offer.
     * @param loginRequest a JSON-formed request to the application containing
     *                     login and password pair.
     * @return a ResponseEntity object with the LoginResponse object as the response body.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Controller received {}", loginRequest);

        LoginResponse tokenOffer = userService.initializeSession(loginRequest);
        log.trace("Controller returns {}", tokenOffer);

        return ResponseEntity.ok(tokenOffer);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        String userToExit = principal.getName();
        log.debug("logout request for {}", userToExit);

        userService.terminateSession(userToExit);
        SecurityContextHolder.clearContext();

        log.debug("Controller sends OK to log {} out", userToExit);
        return ResponseEntity.ok().build();
    }

}
