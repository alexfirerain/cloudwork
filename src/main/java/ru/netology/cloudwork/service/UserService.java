package ru.netology.cloudwork.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginDto;
import ru.netology.cloudwork.dto.UserDto;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager for user tokens and sessions.
 */
@Service
public class UserService implements UserDetailsService {

    public UserService(IdentityService identityService, UserRepository userRepository) {
        this.identityService = identityService;
        this.userRepository = userRepository;
    }

    /**
     * Mappings between token and username.
     */
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private final IdentityService identityService;
    private final UserRepository userRepository;

    public LoginDto initializeSession(UserDto loginRequest) {
        String usernameRequested = loginRequest.getUsername();
        Optional<UserEntity> user = userRepository.findByUsername(usernameRequested);

        if (user.isEmpty())
            throw new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");

        if (!user.get().getPassword().equals(loginRequest.getPassword()))
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

        return new LoginDto(token);
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
        return null;
    }
}
