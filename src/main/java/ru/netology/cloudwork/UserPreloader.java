package ru.netology.cloudwork;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.service.UserService;

import java.util.List;

/**
 * Just an addl utility to have some users preloaded
 * while testing and checking the CloudWork works.
 * It contains some predefined users; when da app starts and
 * preloading enabled, each got checked if such a username
 * already in the DB and, if not, got saved into there.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("application.user-preloader.enabled")
public class UserPreloader implements CommandLineRunner {

    private final UserService userService;

    List<UserEntity> users = List.of(
        // add your users here
        new UserEntity("user", "0000")
    );

    @Override
    public void run(String... args) throws Exception {
        users.stream()
                .filter(x -> !userService.isUserPresent(x.getUsername()))
                .forEach(userService::createUser);

    }
}
