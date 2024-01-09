package ru.netology.cloudwork.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The implementation of {@link Authentication}, a representation of
 * user's state of being authenticated against the CloudWorkToken model.
 * It holds the username & password from the corresponding user-entity
 * along with its set of roles, and it can be authenticated or not.
 */
public class CloudworkAuthorization implements Authentication {
    private final String username;
    private final String password;
    private boolean authenticated;
    private final Set<Role> authorities;

    public CloudworkAuthorization(UserDetails userDetails, boolean isOn) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authenticated = isOn;
        authorities = userDetails.getAuthorities().stream()
                .map(e -> (Role) e).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * The constructor crafting a non-authenticated authentication instance.
     * @param userDetails   user data set to be authenticated.
     */
    public CloudworkAuthorization(UserDetails userDetails) {
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
