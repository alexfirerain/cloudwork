package ru.netology.cloudwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDto implements Serializable {
    private String message;
    private int id;

}
