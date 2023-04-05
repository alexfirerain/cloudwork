package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
@NoArgsConstructor
@Table(name = "files",
        uniqueConstraints =  @UniqueConstraint(
                columnNames = { "file_name", "owner_user_id" }))
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @NotBlank
    private String fileName;

    @PositiveOrZero
    private Long size;

    private int hash;

    private String fileType;

    @NotNull
    @ManyToOne
    private UserEntity owner;

    @Lob @Basic(fetch = LAZY)
    private byte[] body;

    public FileEntity(MultipartFile file, UserEntity owner) throws IOException {
        this.fileName = file.getOriginalFilename();
        this.size = file.getSize();
        this.hash = file.hashCode();
        this.fileType = file.getContentType();
        this.owner = owner;
        this.body = file.getBytes();
    }

    public FileEntity(UserEntity owner, String fileName, MultipartFile file) throws IOException {
        this.fileName = fileName;
        this.size = file.getSize();
        this.hash = file.hashCode();
        this.fileType = file.getContentType();
        this.owner = owner;
        this.body = file.getBytes();
    }
}
