package ru.netology.cloudwork.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class ErrorDto {
    private final String message;
    private final int id;

    public ErrorDto(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public String message() {
        return message;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ErrorDto) obj;
        return Objects.equals(this.message, that.message) &&
                this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, id);
    }

    @Override
    public String toString() {
        return "ErrorDto[" +
                "message=" + message + ", " +
                "id=" + id + ']';
    }


}
