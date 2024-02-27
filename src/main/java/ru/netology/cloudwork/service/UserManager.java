package ru.netology.cloudwork.service;

import jakarta.validation.constraints.NotNull;
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

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A service for managing and getting user information
 * such as assigned tokens for CloudWork sessions and {@link UserDetails}.
 * It is a {@link UserDetailsService} usable by Spring Security Authentication mechanism.
 * In CloudWork implementation this means communicating with {@link CloudworkAuthorizationService}.
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
        log.trace("UserService is asked for '{}'", username);
        UserInfo userInfo = new UserInfo(getUserByUsername(username));
        log.trace("User {} found in the base", userInfo);
        if (userInfo.getAuthorities().isEmpty()) {
            log.warn("User '{}'s authorities not defined", username);
            throw new UsernameNotFoundException("Полномочия пользователя не определены.");
        }
        return userInfo;
    }

    /**
     * Takes a user entity, encrypts its password and transfers
     * to repository for saving.
     *
     * @param user an almost ready entity.
     */
    public void putUser(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Tells whether such a username is present in the DB.
     * @param username a name of user to be checked.
     * @return  boolean signifying user's presence.
     */
    public boolean isUserPresent(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Returns a token string which saved in DB for the user specified.
     *
     * @param username the user in question.
     * @return the string that is user's token, including {@code null}
     * both if the token is null or specified user is absent from DB.
     * @throws IllegalArgumentException if a given user is null.
     */
    public String findTokenByUsername(@NotNull String username) {
        return userRepository.findTokenByUsername(username).orElse(null);
    }

    /**
     * Sets a specified token string (or {@code null}) into relation with the certain user.
     *
     * @param username a user who is to be tokenized.
     * @param token    a token being assigned to the user's session.
     */
    public void setToken(String username, String token) {
        userRepository.setAccessToken(username, token);
        log.debug("Token {} mapped and stored for user {}", token, username);
    }

    /**
     * Sets token corresponding to the user to null.
     * If user doesn't exist, does nothing and returns {@code false}
     *
     * @param username the user whose session to be nullified.
     * @return {@code false} if no user with the given name in DB,
     * {@code true} if such a user exists and whose token is set to null now.
     */
    public boolean purgeSession(String username) {
        if (!isUserPresent(username)) return false;
        setToken(username, null);
        return true;
    }

    /**
     * Delivers the {@link UserEntity} corresponding to a username given.
     *
     * @param username                      a user's name given.
     * @return a UserEntity from DB, having the specified username.
     * @throws UsernameNotFoundException if no entity with the given login found.
     */
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь '%s' не зарегистрирован."
                                .formatted(username)));
    }

    /**
     * Creates and returns mapping between currently active tokens and users.
     * @return  a map where keys are tokens and values are corresponding UserInfo objects.
     */
    public Map<String, UserDetails> getActiveSessions() {
        return userRepository
                .getActiveSessions().stream()
                .collect(Collectors
                        .toMap(objects -> (String) objects[1],
                                objects -> loadUserByUsername((String) objects[0]),
                                (a, b) -> b));
    }
}
