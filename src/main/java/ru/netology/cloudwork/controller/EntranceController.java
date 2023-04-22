package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.service.UserService;

/**
 * A controller for user login and logout.
 */
@RestController
@Slf4j
public class EntranceController {

    private final UserService userService;

    public EntranceController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Controller received {}", loginRequest);

        LoginResponse tokenOffer = userService.initializeSession(loginRequest);
        log.trace("Controller returns {}", tokenOffer);

        return ResponseEntity.ok(tokenOffer);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Controller logging out for {}", username);

        userService.terminateSession(username);
        SecurityContextHolder.clearContext();

        log.debug("Controller sends OK to log {} out", username);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/logout")
//    public ResponseEntity<?> logout() {
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/login")
    public ResponseEntity<?> loginLogout() {
        log.info("Logging out for {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

}
