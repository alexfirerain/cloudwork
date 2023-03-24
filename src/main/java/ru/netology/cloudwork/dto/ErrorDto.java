package ru.netology.cloudwork.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public record ErrorDto(String message, int id) {

}
