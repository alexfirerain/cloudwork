package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
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
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

//    public UserService(IdentityService identityService, UserRepository userRepository) {
//        this.identityService = identityService;
//        this.userRepository = userRepository;
//    }

    /**
     * Mappings between token and username.
     */
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final IdentityService identityService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();


    public LoginResponse initializeSession(LoginRequest loginRequest) {
        String usernameRequested = loginRequest.getUsername();
        UserInfo user = (UserInfo) loadUserByUsername(usernameRequested);

        if (!encoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Неверный пароль.");

        String token = sessions.entrySet().stream()
                        .filter(entry -> usernameRequested.equals(entry.getValue()))
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(null);

        if (token == null) {
            token = identityService.generateTokenFor(usernameRequested);
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
        Optional<UserEntity> entity = userRepository.findByUsername(username);
        entity.orElseThrow(() ->
                new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован."));
        return entity.map(UserInfo::new).get();
    }

    public UserEntity createUser(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
