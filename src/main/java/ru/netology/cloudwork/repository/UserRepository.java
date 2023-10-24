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

    @Query("SELECT u.accessToken FROM UserEntity u WHERE u.username = :username")
    Optional<String> findTokenByUsername(@NotNull @Param("username") String username);

    /**
     * Finds and returns the {@link UserEntity} by its current session token.
     * @param accessToken a string being used as an identifying token.
     * @return  an optional with the UserEntity found, empty one if none.
     */
    Optional<UserEntity> findByAccessToken(String accessToken);

    /**
     * Writs new token (or {@code null}) as assigned to the user.
     * @param username  the user the token is getting mapped to.
     * @param token     the token being mapped to the user.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accessToken = :token WHERE u.username = :username")
    void setAccessToken(@NotNull @Param("username") String username, @Param("token") String token);

    /**
     * Deletes user from the database.
     * @param name  username of the user being deleted.
     */
    @Modifying
    @Query("DELETE UserEntity u WHERE u.username = :name")
    void deleteByUsername(@NotNull @Param("name") String name);

    boolean existsByUsername(@NotNull @Param("username") String username);
}
