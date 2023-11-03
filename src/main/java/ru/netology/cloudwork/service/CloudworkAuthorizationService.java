package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.model.CloudworkAuthorization;
import ru.netology.cloudwork.model.UserInfo;

import java.util.Date;

/**
 * A manager for user tokens and sessions. Also a custom {@link AuthenticationManager
 * AuthenticationManager} implementation in the CloudWork.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudworkAuthorizationService implements AuthenticationManager {

    /**
     * A regular encoder to cipher passwords when storing in database.
     */
    private final PasswordEncoder encoder;
    /**
     * A custom {@link UserDetailsService UserDetailsService}
     * implementation in the CloudWork.
     */
    private final UserManager userManager;

    /**
     * Tries to open user session by verifying the username and password,
     * then generates a token for the newcomer and returns it,
     * or uses the existing token
     * if the user has already been logged and did not exit.
     * Current realization of user-preloading however purges tokens
     * when the application starts.
     *
     * @param loginRequest a DTO containing login information.
     * @return a DTO with token for user to use.
     * @throws UsernameNotFoundException if login received not known.
     * @throws BadCredentialsException   if known password for that login doesn't match the one provided.
     */
    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.getLogin();
        log.trace("Logging {} in", usernameRequested);
        UserDetails user = userManager.loadUserByUsername(usernameRequested);
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.trace("Password provided makes no match");
            throw new BadCredentialsException("Неверный пароль.");
        }
        String token = userManager.findTokenByUsername(usernameRequested);
        if (token == null) {
            token = generateTokenFor(user);
            log.debug("Token '{}' generated for '{}'. A session established...", token, usernameRequested);
            userManager.setToken(usernameRequested, token);
        } else {
            log.debug("User '{}' has already been logged in with '{}'. Joining the session...", usernameRequested, token);
        }
        return new LoginResponse(token);
    }

    public void authenticateByToken(String token) {
        UserInfo user = userManager.findUserByToken(token);
        if (user == null) {
            log.warn("No mapped user for the token");
            throw new BadCredentialsException("Сеанс пользователя завершён.");
        }
        log.trace("User by token found: {}", user.getUsername());
        Authentication auth = authenticate(new CloudworkAuthorization(user));
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("User '{}' got authenticated for the request", auth.getPrincipal());
    }

    /**
     * Cleans in the DB a token for user specified.
     *
     * @param username a user whose token to be nullified.
     */
    public void terminateSession(String username) {
        log.debug("Terminating {} session", username);
        log.info(userManager.purgeSession(username) ?
                "Session for '{}' terminated" :
                "User '{}' not found", username);
    }

    /**
     * Generates a simplified CloudWork-token for a user session.
     * The resulting string consists of username and current date.
     *
     * @param authentication данные о пользователе.
     * @return the string of user's name and login moment.
     */
    private String generateTokenFor(UserDetails authentication) {
        String token = "%s @ %s".formatted(authentication.getUsername(), new Date());
        log.trace("Token '{}' generated", token);
        return token;
    }

    /**
     * Attempts to authenticate the passed {@link Authentication} object, returning a
     * fully populated <code>CloudworkAuthorization</code> object (including granted authorities)
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
     * @param authentication the CloudworkAuthorization request object
     * @return a fully authenticated CloudworkAuthorization object with credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserDetails user = userManager.loadUserByUsername((String) authentication.getPrincipal());

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
