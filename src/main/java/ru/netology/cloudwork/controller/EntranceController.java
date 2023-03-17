package ru.netology.cloudwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudwork.dto.UserDto;

@RestController
public class EntranceController {
    @PostMapping("/login")
    public ResponseEntity<String> authenticationLogin(@RequestBody UserDto userDto) {

        return ResponseEntity.ok("hello");
    }
}
