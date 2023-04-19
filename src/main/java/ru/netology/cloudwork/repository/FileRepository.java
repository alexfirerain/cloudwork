package ru.netology.cloudwork.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudwork.entity.FileEntity;
import ru.netology.cloudwork.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * The FileRepository is ruling in the Kingdom of Files.
 * Files are stored in some kind of DataBase, but JPA magic does not care
 * of its actual implementation since it masters the art of SpEL.
 */
@Repository
@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByOwner(@NotNull UserEntity owner);

    Optional<FileEntity> findByFileId(long id);

    /**
     * Spells the cast to change a file's name to whatever you want
     * when you know its name and owner.
     * @param owner   a name of owner.
     * @param oldName   a name of file.
     * @param newName   a name the file will bear from now on.
     */
    @Modifying
    @Query("update FileEntity f set f.fileName = :newName WHERE f.owner.username = :owner and f.fileName = :oldName")
    void renameFile(@Param("owner") String owner,
                    @Param("oldName") String oldName,
                    @Param("newName") String newName);

    /**
     * Spells the cast to obtain a file when you know its name and owner.
     * @param owner a name of owner.
     * @param fileName  a name of file.
     * @return  an optional presentation of the found {@link FileEntity}.
     */
    @Query("select f from FileEntity f WHERE f.owner.username = :owner AND f.fileName = :fileName")
    Optional<FileEntity> findByOwnerAndFilename(@NotNull @Param("owner") String owner,
                                                @NotNull @Param("fileName") String fileName);

}
