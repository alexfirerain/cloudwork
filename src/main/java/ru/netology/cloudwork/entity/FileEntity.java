package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @NotBlank
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    @NotBlank
    private String fileType;
    @PositiveOrZero
    private int size;

    @NotNull
    @ManyToOne
    private UserEntity owner;

    @Lob
    private byte[] body;

}
