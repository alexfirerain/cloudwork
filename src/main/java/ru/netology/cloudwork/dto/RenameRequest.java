package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class RenameRequest {
    @NotBlank(message = "во что переименовывать?")
    private String filename;
}
