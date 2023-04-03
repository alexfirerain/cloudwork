package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A request to be logged in from client to the server side.
 */
@Data
@NoArgsConstructor
@Getter
public class LoginRequest {
    @NotBlank(message = "Не указано имя.")
    private String login;

    @NotNull(message = "Не указан пароль.")
    private String password;
}
