package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
@NoArgsConstructor
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @NotBlank
    private String fileName;

    @PositiveOrZero
    private Long size;

    private int hash;

    @NotNull
    @ManyToOne
    private UserEntity owner;

    @Lob @Basic(fetch = LAZY)
    private byte[] body;

    public FileEntity(File file, UserEntity owner) throws IOException {
        this.fileName = file.getName();
        this.size = file.length();
        this.hash = file.hashCode();
        this.owner = owner;
        this.body = Files.readAllBytes(file.toPath());
    }
}
