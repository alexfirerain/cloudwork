package ru.netology.cloudwork.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloudwork.entity.Role;
import ru.netology.cloudwork.entity.UserEntity;

import java.util.Collection;
import java.util.Set;

@Setter
@Getter
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
        this.authorities = storedUser.getAuthorities();
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
