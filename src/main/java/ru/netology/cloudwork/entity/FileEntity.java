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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Column(name = "upload_date", nullable = false)
    private Date uploadDate;

    @Column(name = "update_date", nullable = false)
    private Date updateDate;

    /**
     * A constructor for practical creation of file entity based on the
     * data normally got to the app when a file is uploaded.
     * @param owner    a name of user who is uploading.
     * @param fileName a name of file being loaded.
     * @param file     a MultipartFile entity being loaded.
     * @throws IOException  if the process occur to abort for some why.
     */
    public FileEntity(@NotNull UserEntity owner, String fileName, MultipartFile file) throws IOException {
        this.fileName = fileName;
        this.size = file.getSize();
        this.fileType = file.getContentType();
        this.owner = owner;
        this.body = file.getBytes();
        this.uploadDate = new Date();
        this.updateDate = uploadDate;
    }

    /**
     * An auxiliary generator of FileEntities explicitly setting ID, ignoring file type,
     * and setting a date as is defined by a string looking like "yyyy-MM-dd HH:mm:ss".
     * @param id       a file ID.
     * @param owner    an entity's master.
     * @param fileName  a name.
     * @param body  bytes building a body of file.
     * @param date  an arbitrary date as a string "yyyy-MM-dd HH:mm:ss".
     * @return  a new FileEntity object with properties specified.
     */
    public static FileEntity getForData(Long id, @NotNull UserEntity owner, String fileName, byte[] body, String date) {
        FileEntity entity = new FileEntity();
        entity.setFileId(id);
        entity.setFileName(fileName);
        entity.setSize((long) body.length);
        entity.setOwner(owner);
        entity.setBody(body);
        try {
            entity.setUploadDate(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
        } catch (ParseException e) {
            entity.setUploadDate(new Date());
        }
        entity.setUpdateDate(entity.getUploadDate());
        return entity;
    }

}

