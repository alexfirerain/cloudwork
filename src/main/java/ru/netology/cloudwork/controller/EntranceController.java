package ru.netology.cloudwork.controller;

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
//@CrossOrigin(origins = "http://localhost:8080")
public class EntranceController {

    private final UserService userService;

    public EntranceController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        log.debug("Controller received {}", loginRequest);

        return ResponseEntity.ok(userService.initializeSession(loginRequest));
//        return ResponseEntity.ok(new LoginResponse("пизда"));
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("auth-token") String token) {
        userService.terminateSession(token);
        return ResponseEntity.ok().build();
    }


}
