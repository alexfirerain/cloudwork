package ru.netology.cloudwork.service;

import jakarta.annotation.PostConstruct;
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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager for user tokens and sessions, also a custom {@link AuthenticationManager
 * AuthenticationManager} implementation in the CloudWork.
 * It also maintains an in-memory map of active tokens and their associated
 * currently logged-in users for quicker response.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
     * In-memory representation of active token-user mappings
     * which allows the CloudworkAuthorizationService
     * to quickly authenticate users without having to query the database each time.
     */
    private static final Map<String, UserDetails> SESSIONS = new ConcurrentHashMap<>();


    /**
     * On bean's creation loads into memory active sessions
     * as supplied by the current state of the DB.
     * It sets the Service in state of being ready to serve.
     */
    @PostConstruct
    public void initializeMap() {
        SESSIONS.putAll(userManager.getActiveSessions());
    }

    /**
     * Tries to open user session by verifying the username and password,
     * then generates a token for the newcomer and returns it,
     * or uses the existing token
     * if the user has already been logged and did not exit.
     *
     * @param loginRequest a DTO containing login information.
     * @return a DTO with token for user to use.
     * @throws UsernameNotFoundException if login received not known.
     * @throws BadCredentialsException   if known password for that login doesn't match the one provided.
     */
    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.login();
        log.trace("Logging {} in", usernameRequested);
        UserDetails user = userManager.loadUserByUsername(usernameRequested);
        if (!encoder.matches(loginRequest.password(), user.getPassword())) {
            log.trace("Password provided makes no match");
            throw new BadCredentialsException("Неверный пароль.");
        }
        String token = userManager.findTokenByUsername(usernameRequested);
        if (token == null) {
            token = generateTokenFor(user);
            log.debug("Token '{}' generated for '{}'. A session established...", token, usernameRequested);
            userManager.setToken(usernameRequested, token);
            SESSIONS.put(token, user);
            log.info("CloudWork session for user '{}' started.", usernameRequested);
        } else {
            log.debug("User '{}' has already been logged in with '{}'. Joining the session...", usernameRequested, token);
            log.info("CloudWork session for user '{}' continued.", usernameRequested);
        }
        return new LoginResponse(token);
    }


    /**
     * Authenticates a user by token, retrieving the user information from in-memory map.
     * Then, if found, the user gets authenticated and the authentication information
     * is stored against the SecurityContextHolder.
     *
     * @param token a token string supplied by the request to be authenticated.
     * @throws BadCredentialsException if there's no mapped user for such a token.
     */
    public void authenticateByToken(String token) {
        UserDetails user = SESSIONS.get(token);
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
        String token = userManager.findTokenByUsername(username);
        if (token != null) {
            log.trace("Terminating {} session", username);
            SESSIONS.remove(token);
            userManager.purgeSession(username);
            log.debug("Session for '{}' terminated", username);
        } else {
            log.warn("User '{}' has no session to be terminated", username);
        }
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
            throw new BadCredentialsException("Для этого юзаря нет активного токена");

        authentication.setAuthenticated(true);

        return authentication;
    }
}
