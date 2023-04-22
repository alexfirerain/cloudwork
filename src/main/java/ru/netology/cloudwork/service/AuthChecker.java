package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.model.UserInfo;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthChecker implements AuthenticationManager {

    private final UserManager userManager;

    /**
     * Attempts to authenticate the passed {@link Authentication} object, returning a
     * fully populated <code>LoggedIn</code> object (including granted authorities)
     * if successful.
     * <p>
     * An <code>AuthenticationManager</code> honour the following contract concerning
     * exceptions:
     * <ul>
     * <li>A {@link DisabledException} will be thrown if an account is disabled.</li>
     * <li>A {@link LockedException} will be thrown if an account is locked.</li>
     * <li>A {@link BadCredentialsException} is sure to be thrown if incorrect
     * credentials come.</li>
     * </ul>
     *
     * @param authentication the LoggedIn request object
     * @return a fully authenticated LoggedIn object with credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserInfo user = (UserInfo) userManager.loadUserByUsername((String) authentication.getPrincipal());

        if (!user.isEnabled())
            throw new DisabledException("Аккаунт отключён");

        if (!user.isAccountNonLocked())
            throw new LockedException("Аккаунт заблокирован");

        if (userManager.findTokenByUsername(user.getUsername()) == null)
            throw new BadCredentialsException("Для этого юзера нет активного токена");

        authentication.setAuthenticated(true);

        return authentication;
    }
}
