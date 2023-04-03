package ru.netology.cloudwork;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.netology.cloudwork.entity.UserEntity;
import ru.netology.cloudwork.service.UserService;

@Component
@RequiredArgsConstructor
public class UserPreloader implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (!userService.isUserPresent("user"))
            userService.createUser(new UserEntity("user", "0000"));
    }
}
