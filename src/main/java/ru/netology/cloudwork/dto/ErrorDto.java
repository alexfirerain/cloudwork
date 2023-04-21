package ru.netology.cloudwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDto implements Serializable {
    private String message;
    private int id;

    /**
     * An all-through numerator of errors in da app.
     * The generated sequence does not persist and starts over
     * with every application run.
     */
    private static final AtomicInteger idCount = new AtomicInteger();

    public ErrorDto(String message) {
        this.message = message;
        id = idCount.getAndIncrement();
    }

    public ErrorDto(Exception cause) {
        this(cause.getLocalizedMessage());
    }

}
