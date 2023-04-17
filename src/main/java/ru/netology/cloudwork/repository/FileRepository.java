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

@Repository
@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByOwner(@NotNull UserEntity owner);

    Optional<FileEntity> findByFileId(long id);

    @Modifying
    @Query("update FileEntity f set f.fileName = :newName WHERE f.owner.username = :owner and f.fileName = :oldName")
    void renameFile(@Param("owner") String owner,
                    @Param("oldName") String oldName,
                    @Param("newName") String newName);

    @Query("select f from FileEntity f WHERE f.owner.username = :owner AND f.fileName = :fileName")
    Optional<FileEntity> findByOwnerAndFilename(@NotNull @Param("owner") String owner,
                                                @NotNull @Param("fileName") String fileName);

}
