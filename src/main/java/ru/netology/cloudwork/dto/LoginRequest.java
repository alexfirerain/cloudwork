package ru.netology.cloudwork.dto;

import lombok.Data;

/**
 * A request to be logged in from client to the server side.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
