package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A presentation of file's name and size
 * to be transferred in a DTO letter.
 */
@Getter
@AllArgsConstructor
public class FileInfo {
    private String filename;
    @PositiveOrZero
    private long size;

    /**
     * A static factory method to have the object ctreated
     * based on two-object array supplied by the DB result set.
     * @param inputData an array of two object, implying the first is string of name, the second long of size.
     * @return  a new populated FileInfo object describing a file entity in the base.
     */
    public static FileInfo fromObjectArray(Object[] inputData) {
        if (inputData.length != 2)
            throw new IllegalArgumentException("Неверное количество аргументов");
        return new FileInfo((String) inputData[0], (Long) inputData[1]);
    }

}
