package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.dto.LoginResponse;
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
    private final Map<String, String> sessions = new ConcurrentHashMap<>(); // can be SQL-saved
    private final IdentityService identityService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final UserManager userManager;




    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.getLogin();
        UserInfo user = (UserInfo) userManager.loadUserByUsername(usernameRequested);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameRequested, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // TODO: move this check to Identity Machine?
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Неверный пароль.");

        String token = sessions.entrySet().stream()
                        .filter(entry -> usernameRequested.equals(entry.getValue()))
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(null);

        if (token == null) {
            token = identityService.generateTokenFor(authentication);
            sessions.put(token, usernameRequested);
        }

        return new LoginResponse(token);
    }

    public String defineUsernameByToken(String token) {
        String username = sessions.get(token);
        if (username == null)
            throw new AuthenticationCredentialsNotFoundException("Сессия окончена.");
        return username;
    }

    public void terminateSession(String token) {

        sessions.remove(token);
    }

}
