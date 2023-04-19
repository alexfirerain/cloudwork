package ru.netology.cloudwork.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * An implementation of {@link GrantedAuthority} in the CloudWorkToken model.
 * It defines role {@code USER} for regular CloudWork's clients
 * and role {@code SUPERUSER} for special management routines.
 * The enum defines no additional properties but the name of the constant itself.
 */
public enum Role implements GrantedAuthority {
    USER,
    SUPERUSER;


    /**
     * This <code>GrantedAuthority</code> can be represented as a <code>String</code>
     * so this method returns a <code>String</code> equal to the name of the role.
     *
     * @return a name of the granted authority.
     */
    @Override
    public String getAuthority() {
        return name();
    }

}
