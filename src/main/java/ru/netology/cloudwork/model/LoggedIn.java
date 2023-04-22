package ru.netology.cloudwork.model;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The implementation of {@link Authentication}, a representation of
 * user's state of being authenticated against the CloudWorkToken model.
 * It holds the username & password from the corresponding user-entity
 * along with its set of roles, and it can be authenticated or not.
 */
@Data
public class LoggedIn implements Authentication {
    private String username;
    private String password;
    private boolean authenticated;
    private Set<Role> authorities;

    public LoggedIn(UserInfo userDetails, boolean isOn) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authenticated = isOn;
        authorities = new HashSet<>();      // can't solve casting otherway
        userDetails.getAuthorities().forEach(e -> authorities.add((Role) e));
    }

    public LoggedIn(UserInfo userDetails) {
        this(userDetails, false);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }


    @Override
    public String getName() {
        return username;
    }
}
