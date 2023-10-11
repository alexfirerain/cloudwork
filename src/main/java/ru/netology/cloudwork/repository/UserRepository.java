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
    /**
     * Provides the {@link UserEntity} defined by unique username.
     * @param username a name to identify user.
     * @return an optional with the UserEntity found, empty one if not.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds and returns the {@link UserEntity} by its current session token.
     * @param accessToken a string 
     * @return
     */
    Optional<UserEntity> findByAccessToken(String accessToken);

    @Modifying
    @Query("UPDATE UserEntity u SET u.accessToken = :token WHERE u.username = :username")
    void setAccessToken(@NotNull @Param("username") String username, @Param("token") String token);

    @Modifying
    @Query("DELETE UserEntity u WHERE u.username = :name")
    void deleteByUsername(@NotNull @Param("name") String name);

}
