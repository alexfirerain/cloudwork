package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A model of user to be stored in a base.
 */
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class UserEntity {

    /**
     * A practical constructor for convenient user creation.
     * @param username  a username.
     * @param password  a password.
     * @param authorities   a set of roles.
     */
    public UserEntity(String username, String password, Role... authorities) {
        this.username = username;
        this.password = password;
        this.authorities = Arrays.stream(authorities).map(Role::getAuthority).collect(Collectors.joining(","));
    }

    /**
     * A practical constructor for convenient user creation
     * defaulting a role to just being a USER.
     * @param username  a username.
     * @param password  a password.
     */
    public UserEntity(String username, String password) {
        this(username, password, Role.USER);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String username;

    private String password;

    /**
     * A string containing a CSV list of {@link Role Roles}.
     */
    private String authorities;

    @OneToMany(mappedBy ="owner", fetch = FetchType.LAZY)
    private List<FileEntity> files;

    @Column(name = "account_expired")
    private boolean accountExpired = false;

    private boolean locked = false;

    @Column(name = "credentials_expired")
    private boolean credentialsExpired = false;     // rename as interface methods?

    private boolean enabled = true;

}
