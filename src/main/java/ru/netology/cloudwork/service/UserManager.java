package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.model.UserInfo;
import ru.netology.cloudwork.repository.UserRepository;

import java.util.Optional;

/**
 * A manager for user storing and getting.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManager implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

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

        UserInfo userInfo = entity.map(UserInfo::new).orElseThrow(() -> {
            log.warn("Username {} not found", username);
            return new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");
        });
        log.info("User {} found", userInfo);

        if (userInfo.getAuthorities().isEmpty()) {
            log.warn("User {} authorities not defined", username);
            throw new UsernameNotFoundException("Полномочия пользователя не определены.");
        }
        return userInfo;
    }

    /**
     * Takes a user entity, encrypts its password and transfers
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
