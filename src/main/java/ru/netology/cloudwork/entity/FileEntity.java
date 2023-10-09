package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static jakarta.persistence.FetchType.LAZY;

/**
 * Entity that represents file stored in the cloud.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files",
        uniqueConstraints =  @UniqueConstraint(
                columnNames = { "file_name", "owner_user_id" }))
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @NotBlank
    @Column(name = "file_name")
    private String fileName;

    @PositiveOrZero
    private Long size;

    private String fileType;

    @NotNull
    @ManyToOne
    private UserEntity owner;

    @Lob
    @Basic(fetch = LAZY)
    @Column(columnDefinition = "LONGBLOB NOT NULL")
    private byte[] body;

    public FileEntity(@NotNull UserEntity owner, String fileName, MultipartFile file) throws IOException {
        this.fileName = fileName;
        this.size = file.getSize();
        this.fileType = file.getContentType();
        this.owner = owner;
        this.body = file.getBytes();
    }
}

