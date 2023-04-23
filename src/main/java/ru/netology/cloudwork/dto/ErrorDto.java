package ru.netology.cloudwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The error response used across the CloudWork.
 * Each error message gets numbered with all-through id.
 */
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

    /**
     * Sets initial value for the all-through numeration.
     * Intended to be used when continuing against a stored state of error logging.
     * @param count error number to be assigned to the next ErrorDto.
     */
    public static void setCount(int count) {
        idCount.set(count);
    }

    public ErrorDto(String message) {
        this.message = message;
        id = idCount.getAndIncrement();
    }

    /**
     * Creates ErrorDto based on caught exception's message.
     * @param cause the exception being wrapped.
     */
    public ErrorDto(Exception cause) {
        this(cause.getLocalizedMessage());
    }

    @Override
    public String toString() {
        return "Error#%d: %s"
                .formatted(id, message);
    }


}
