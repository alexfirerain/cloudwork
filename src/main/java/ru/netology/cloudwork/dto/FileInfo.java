package ru.netology.cloudwork.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.netology.cloudwork.entity.FileEntity;

@Getter
@AllArgsConstructor
public class FileInfo {
    private String name;
    private long size;

    /**
     * Converts a FileEntity object into a FileInfo.
     * @param fileEntity    a file entity in DB-format.
     */
    public FileInfo(@NotNull FileEntity fileEntity) {
        this.name = fileEntity.getFileName();
        this.size = fileEntity.getSize();
    }
}
