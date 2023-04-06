package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginRequest;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.model.UserInfo;
import ru.netology.cloudwork.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager for user tokens and sessions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    /**
     * Mappings between token and username.
     */
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final IdentityService identityService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;




    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.getLogin();
        UserInfo user = (UserInfo) loadUserByUsername(usernameRequested);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameRequested, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);


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

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserService is asked for {}", username);

        Optional<UserEntity> entity = userRepository.findByUsername(username);
        entity.orElseThrow(() -> {
            log.warn("Username {} not found", username);
            return new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");
        });
        UserInfo userInfo = entity.map(UserInfo::new).get();
        log.info("User {} found", userInfo);

        if (userInfo.getAuthorities().isEmpty()) {
            log.warn("User {} authorities not defined", username);
            throw new UsernameNotFoundException("Полномочия пользователя не определены.");
        }
        return userInfo;
    }

    /**
     * Takes an user entity, encrypts its password and transfers
     * to repository for saving.
     * @param user an almost ready entity.
     */
    public void createUser(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean isUserPresent(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
