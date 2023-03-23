package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudwork.dto.LoginDto;
import ru.netology.cloudwork.dto.UserDto;

@RestController
public class EntranceController {
    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody UserDto userDto) {

        String token = userService.generateToken(userDto);

        return ResponseEntity.ok(new LoginDto(token));
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(String token) {
        userService.stopSession(token);
        return ResponseEntity.ok().build();
    }


}
