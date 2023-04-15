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
     * Locates the user based on the username against DB. The <code>UserDetails</code>
     * object that comes back is a {@link UserInfo} object.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated UserInfo instance (never <code>null</code>)
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

    /**
     * Returns UserInfo representation of the user
     * mapped to the token in question
     * or null if none.
     * @param token token to be identified as a mark of user session.
     * @return a UserDetail-featured object for the user mapped to the token
     * or {@code null} if the token is null or not found in the DB.
     */
    public UserInfo findUserByToken(String token) {
        return token == null ? null :
                userRepository
                .findByAccessToken(token)
                .map(UserInfo::new)
                .orElse(null);
    }
}
