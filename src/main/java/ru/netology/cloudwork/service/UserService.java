package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.model.LoggedIn;
import ru.netology.cloudwork.model.UserInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager for user tokens and sessions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    /**
     * Mappings between token and username.
     */
//    private final Map<String, String> sessions = new ConcurrentHashMap<>(); // can be SQL-saved
    private final IdentityService identityService;
    private final PasswordEncoder encoder;
//    private final AuthenticationManager authenticationManager;
    private final UserManager userManager;


    /**
     * Tries to open user session by verifying the username and password,
     * then asks the identity service to generate a token for the newcomer.
     * Or sends back the existing token if the user already logged.
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
            token = identityService.generateTokenFor(user);
            log.debug("Token {} generated for {}", token, usernameRequested);
            userManager.setToken(usernameRequested, token);
        }

        return new LoginResponse(token);
    }

//    public String defineUsernameByToken(String token) {
//        String username = sessions.get(token);
//        if (username == null)
//            throw new AuthenticationCredentialsNotFoundException("Сессия окончена.");
//        return username;
//    }

    public void terminateSession(String username) {

        userManager.setToken(username, null);

    }

}
