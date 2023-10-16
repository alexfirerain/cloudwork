package ru.netology.cloudwork.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudwork.dto.FileInfo;
import ru.netology.cloudwork.entity.FileEntity;

import java.util.List;
import java.util.Optional;

/**
 * The FileRepository is ruling in the Kingdom of Files.
 * Files are stored in some kind of DataBase, but JPA magic does not care
 * of its actual implementation since it masters the art of HQL.
 */
@Repository
@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    /**
     * Spells the cast to change a file's name to whatever you want
     * when you know its name and owner.
     * @param owner   a name of owner.
     * @param oldName   a name of file.
     * @param newName   a name the file will bear from now on.
     */
    @Modifying
    @Query("UPDATE FileEntity f SET f.fileName = :newName" +
            " WHERE f.owner.username = :owner AND f.fileName = :oldName")
    void renameFile(@NotNull @Param("owner") String owner,      // why does this query not work?!
                    @NotNull @Param("oldName") String oldName,
                    @NotNull @Param("newName") String newName);

    /**
     * Spells the cast to change a file's name to whatever you want
     * when you just know its id.
     * @param id    id of the file in concern.
     * @param newName   a name the file will bear from now on.
     */
    @Modifying
    @Query("UPDATE FileEntity f SET f.fileName = :newName " +
            "WHERE f.fileId = :id")
    void renameFile(@Param("id") long id,
                    @NotNull @Param("newName") String newName);


    /**
     * Spells the cast to obtain a file when you know its name and owner.
     * @param owner a name of owner.
     * @param fileName  a name of file.
     * @return  an optional presentation of the found {@link FileEntity}.
     */
    @Query("SELECT f FROM FileEntity f " +
            "WHERE f.owner.username = :owner AND f.fileName = :fileName")
    Optional<FileEntity> findByOwnerAndFilename(@NotNull @Param("owner") String owner,
                                                @NotNull @Param("fileName") String fileName);

    /**
     * Supplies an optional containing ID of the file defined by owner and filename
     * or, if no such file is found, an empty optional.
     * @param owner username of file's owner.
     * @param fileName filename of the matter.
     * @return  an optional with the file's ID in the DB, an empty one if not found.
     */
    @Query("SELECT f.fileId FROM FileEntity f " +
            "WHERE f.owner.username = :owner AND f.fileName = :fileName")
    Optional<Long> findFileIdByOwnerAndFilename(@NotNull @Param("owner") String owner,
                                            @NotNull @Param("fileName") String fileName);

     /**
     * Serves semifinished data for a limited list of files owned by a pointed user.
     * @param username a name of owning user.
     * @param limit    a number of files to be listed.
     * @return  a list of arrays of objects, each array containing filename and size values,
      * that correspond to properties of a {@link FileInfo} DTO, the list is
      * restricted with a limit and ordered by upload date from the newest.
     */
    @Query(value = "SELECT file_name, size FROM files " +
            "WHERE owner_user_id = (SELECT user_id FROM users WHERE username =:username) " +
            "ORDER BY upload_date LIMIT :limit",
            nativeQuery = true)
    List<Object[]> listFiles(@Param("username") String username, @Param("limit") int limit);

}
