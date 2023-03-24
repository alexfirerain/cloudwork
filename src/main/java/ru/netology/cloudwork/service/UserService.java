package ru.netology.cloudwork.service;

import io.micrometer.observation.Observation;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.dto.LoginDto;
import ru.netology.cloudwork.dto.UserDto;

import java.util.Map;
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

    public LoginDto initializeSession(UserDto userDto) {
        String usernameRequested = userDto.getUsername();
        UserEntity user = userRepository.findByUsername(usernameRequested);

        if (user == null)
            throw new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");

        if (!user.getPassword().equals(userDto.getPassword()))
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
