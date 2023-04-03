package ru.netology.cloudwork.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

        return ResponseEntity.ok(userService.initializeSession(loginRequest));
//        return ResponseEntity.ok(new LoginResponse("take-a-token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader(name = "auth-token") String token) {
        log.info("Controller logging out {}", token);
//        userService.terminateSession(token);
        return ResponseEntity.ok().build();
    }


}
