package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * A transfer object to represents a request to rename a file in the storage.
 */
@Validated
public record RenameRequest(@NotBlank(message = "во что переименовывать?") String filename) {}
