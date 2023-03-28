package ru.netology.cloudwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileInfo {
    private String name;
    private int size;
}
