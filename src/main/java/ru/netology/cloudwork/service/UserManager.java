package ru.netology.cloudwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.entity.FileEntity;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.model.UserInfo;
import ru.netology.cloudwork.repository.FileRepository;
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
    private FileRepository fileRepository;

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
        Optional<UserEntity> entity = userRepository.findByUsername(username);

        UserInfo userInfo = entity.map(UserInfo::new).orElseThrow(() -> {
            log.warn("Username '{}' not found", username);
            return new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");
        });
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
     * @param user an almost ready entity.
     */
    public void createUser(UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean isUserPresent(String username) {
        return userRepository.existsByUsername(username);
//                findByUsername(username).isPresent();
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

    /**
     * Returns a token string which saved in DB for the user specified.
     * @param username the user in question.
     * @return  the string that is user's token, including {@code null} if it is null.
     * @throws UsernameNotFoundException if an absent user is requested for.
     */
    public String findTokenByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Юзернейм не найден: " + username));
        return user.getAccessToken();
    }

    /**
     * Sets a specified token string (or {@code null}) into relation with the certain user.
     * @param username a user who is to be tokenized.
     * @param token a token being assigned to the user's session.
     */
    public void setToken(String username, String token) {
        userRepository.setAccessToken(username, token);
        log.debug("Token {} mapped and stored for user {}", token, username);
    }

    /**
     * Sets token corresponding to the user to null.
     * @param username the user whose session to be nullified.
     * @return {@code false} if no user with the given name in DB,
     * {@code true} if such a user exists and whose token is set to null now.
     */
    public boolean purgeSession(String username) {
        if (isUserPresent(username)) {
            setToken(username, null);
            return true;
        }
        return false;
    }

    /**
     * Deletes from DB a user with name specified, also all his/her files.
     * If such a user is not there, throws a corresponding exception.
     * @param name the username of a user to be deleted.
     * @throws UsernameNotFoundException if no user with such a name found.
     */
    public void deleteUser(String name) {
        UserEntity userToDelete = userRepository.findByUsername(name).orElseThrow(() -> {
            log.warn("Username '{}' not found", name);
            return new UsernameNotFoundException("Пользователь с таким именем не зарегистрирован.");
        });

        fileRepository.deleteAllById(
                userToDelete.getFiles().stream()
                        .map(FileEntity::getFileId).toList()
        );

        userRepository.deleteById(userToDelete.getUserId());
    }

}
