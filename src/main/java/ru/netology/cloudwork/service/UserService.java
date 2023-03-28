package ru.netology.cloudwork.service;

import org.springframework.security.authentication.BadCredentialsException;
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
public class UserService {


    /**
     * Mappings between token and username.
     */
    private final Map<String, String> sessions = new ConcurrentHashMap<>();
    private IdentityService identityService;
    private UserRepository userRepository;

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
}
