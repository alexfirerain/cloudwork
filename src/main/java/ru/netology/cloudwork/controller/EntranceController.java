package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.service.UserService;

/**
 * A controller for user login and logout.
 */
@RestController
//@CrossOrigin(origins = "http://localhost:8080")
public class EntranceController {

    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(userService.initializeSession(loginRequest));
//        return ResponseEntity.ok(new LoginResponse("пизда"));
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("auth-token") String token) {
        userService.terminateSession(token);
        return ResponseEntity.ok().build();
    }


}
