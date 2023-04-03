package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * A request to be logged in from client to the server side.
 */
@Data
public class LoginRequest {
    @NotBlank(message = "Не указано имя.")
    private String username;

    @NotNull(message = "Не указан пароль.")
    private String password;
}
