package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record RenameRequest(@NotBlank(message = "во что переименовывать?") String filename) {}
