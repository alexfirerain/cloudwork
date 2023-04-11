package ru.netology.cloudwork.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
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

//    Optional<FileEntity> findByOwnerAndAndFileName(@NotNull UserEntity owner, @NotNull String fileName);

}
