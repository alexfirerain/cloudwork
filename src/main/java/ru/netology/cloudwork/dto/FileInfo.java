package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.PositiveOrZero;

/**
 * A presentation of file's name and size
 * to be transferred in a DTO letter.
 */
public record FileInfo(String filename, @PositiveOrZero Long size) {
    /**
     * A static factory method to have the object created
     * based on two-object array supplied in the result set from DB.
     *
     * @param inputData an array of two object, implying the first is string for name, the second is long for size.
     * @return a new populated FileInfo object representing a file entity from the base.
     */
    public static FileInfo fromObjectArray(Object[] inputData) {
        if (inputData.length != 2)
            throw new IllegalArgumentException("Неверное количество аргументов");
        return new FileInfo((String) inputData[0], (Long) inputData[1]);
    }

}
