package ru.netology.cloudwork.dto;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The error response used across the CloudWork.
 * Each error message gets numbered with all-through id.
 */
public record ErrorDto(String message, int id) {

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

    /**
     * Tailors new DTO from the message to be wrapped and sent.
     * @param message the message in question.
     */
    public ErrorDto(String message) {
        this(message, idCount.getAndIncrement());
    }

    /**
     * Creates ErrorDto based on caught exception's message.
     * @param cause the exception being wrapped.
     */
    public ErrorDto(Throwable cause) {
        this(cause.getLocalizedMessage());
    }

    @Override
    public String toString() {
        return "Error#%d: %s"
                .formatted(id, message);
    }


}
