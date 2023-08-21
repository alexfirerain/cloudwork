package ru.netology.cloudwork;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.service.UserManager;

import java.util.List;

/**
 * Just an addl utility to have some users preloaded
 * while testing and checking the CloudWork works.
 * It contains some predefined users; when da app starts and
 * preloading enabled, each of them got checked if such a username
 * already in the DB and, if not, got saved into there.
 * If a username already present, sets this user's token to null
 * to have a clean new session.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("application.user-preloader.enabled")
public class UserPreloader implements CommandLineRunner {

    private final UserManager userManager;

    List<UserEntity> users = List.of(
        // add your users here
        new UserEntity("user", "0000"),
        new UserEntity("who_user", "1111")
    );

    @Override
    public void run(String... args) {
        if (users == null) return;
        users.stream()
                .filter(x -> !userManager.purgeSession(x.getUsername()))
                .forEach(userManager::createUser);

    }
}
