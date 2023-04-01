package ru.netology.cloudwork.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.KotlinSerializationStringEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Autowired
    private final PasswordEncoder encoder;

    /**
     * A practical constructor for convenient user creation;
     * if encodes
     * @param username
     * @param password
     * @param authorities
     */
    public UserEntity(String username, String password, Role... authorities) {
        this.username = username;
        this.password = encoder.encode(password);
        this.authorities = Set.of(authorities);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true)
    private String username;

    private String password;

    @OneToMany(mappedBy ="owner", fetch = FetchType.EAGER)
    private List<FileEntity> files;
    @OneToMany
    private Set<Role> authorities;

    @Column(name = "account_expired")
    private boolean accountExpired = false;

    private boolean locked = false;

    @Column(name = "credentials_expired")
    private boolean credentialsExpired = false;     // rename as interface methods?

    private boolean enabled = true;

    public UserEntity() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
