package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.model.UserInfo;

import java.util.Date;

/**
 * A manager for user tokens and sessions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserManager userManager;


    /**
     * Tries to open user session by verifying the username and password,
     * then generates a token for the newcomer and returns it,
     * or uses the existing token
     * if the user was already logged and did not exit.
     * @param loginRequest a DTO containing login information.
     * @return  a DTO with token for user to use.
     * @throws UsernameNotFoundException if login received not known.
     * @throws BadCredentialsException if known password for that login doesn't match.
     */
    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.getLogin();
        log.trace("Logging {} in", usernameRequested);
        UserInfo user = (UserInfo) userManager.loadUserByUsername(usernameRequested);

        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.trace("Password provided makes no match");
            throw new BadCredentialsException("Неверный пароль.");
        }

        // if user has seemingly been logged in and token already exists, it gets reused.
        // might be not very secure
        String token = userManager.findTokenByUsername(usernameRequested);

        if (token == null) {
            token = generateTokenFor(user);
            log.debug("Token {} generated for {}", token, usernameRequested);
            userManager.setToken(usernameRequested, token);
        }

        return new LoginResponse(token);
    }


    public void terminateSession(String username) {
        userManager.setToken(username, null);
    }

    private String generateTokenFor(UserInfo authentication) {
        String token = "%s @ %s".formatted(authentication.getUsername(), new Date());
        log.trace("Token '{}' generated", token);
        return token;
    }

}
