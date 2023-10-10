package ru.netology.cloudwork.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudwork.entity.UserEntity;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByAccessToken(String accessToken);

    @Modifying
    @Query("UPDATE UserEntity u SET u.accessToken = :token WHERE u.username = :username")
    void setAccessToken(@NotNull @Param("username") String username, @Param("token") String token);

    @Modifying
    @Query("DELETE UserEntity u WHERE u.username = :name")
    void deleteByUsername(@NotNull @Param("name") String name);

}
