package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

/**
 * A request from a client to be logged to the server side.
 */
@Data
@Getter
@Validated
public class LoginRequest {
    @NotBlank(message = "Не указано имя.")
    private String login;

    @NotNull(message = "Не указан пароль.")
    private String password;

}
