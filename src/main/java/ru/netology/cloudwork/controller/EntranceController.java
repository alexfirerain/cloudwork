package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudwork.dto.LoginDto;
import ru.netology.cloudwork.dto.UserDto;
import ru.netology.cloudwork.service.UserService;

/**
 * A controller for user login and logout.
 */
@RestController
public class EntranceController {


    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody UserDto userDto) {

//        return ResponseEntity.ok(userService.initializeSession(userDto));
        return ResponseEntity.ok(new LoginDto("пизда"));
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestParam("auth-token") String token) {
        userService.terminateSession(token);
        return ResponseEntity.ok().build();
    }


}
