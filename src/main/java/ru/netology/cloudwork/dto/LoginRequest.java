package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * A request from a client to be logged to the server side.
 */
@Validated
public record LoginRequest(@NotBlank(message = "Не указано имя.") String login,
                           @NotNull(message = "Не указан пароль.") String password) {}
