package ru.netology.cloudwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloudwork.entity.UserEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of UserDetails interface in the CloudWork user-management system.
 * Encapsulates properties relevant to authentication but not to file-storing logics.
 */
@Setter
@Getter
@AllArgsConstructor
public class UserInfo implements UserDetails {
    private String username;
    private String password;
    private Set<Role> authorities;
    private boolean accountExpired = false;
    private boolean locked = false;
    private boolean credentialsExpired = false;     // rename as interface methods?
    private boolean enabled = true;

    /**
     * Creates a UserInfo object populated from a database user entity.
     * @param storedUser a user entity in DB-format.
     */
    public UserInfo(UserEntity storedUser) {
        this.username = storedUser.getUsername();
        this.password = storedUser.getPassword();
        this.authorities = Arrays.stream(storedUser.getAuthorities().split(","))
                                    .map(Role::valueOf).collect(Collectors.toSet());
        this.accountExpired = storedUser.isAccountExpired();
        this.locked = storedUser.isLocked();
        this.credentialsExpired = storedUser.isCredentialsExpired();
        this.enabled = storedUser.isEnabled();
    }

    /**
     * Creates a UserInfo object with basic defaults.
     * @param username    a username.
     * @param password  a password.
     * @param authorities a set of roles.
     */
    public UserInfo(String username, String password, Role... authorities) {
        this.username = username;
        this.password = password;
        this.authorities = Set.of(authorities);
    }

    /**
     * A simple constructor defaulting a new USER-role.
     * @param username a username.
     * @param password a password.
     */
    public UserInfo(String username, String password) {
        this(username, password, Role.USER);
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                '}';
    }
}
