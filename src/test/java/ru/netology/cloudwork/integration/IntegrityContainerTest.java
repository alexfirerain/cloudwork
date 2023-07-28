package ru.netology.cloudwork.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudwork.CloudworkApplication;
import org.testcontainers.containers.MySQLContainer;
import ru.netology.cloudwork.dto.LoginRequest;


@Testcontainers
@SpringBootTest(classes = CloudworkApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrityContainerTest {
    private static final int PORT = 9090;
    private static final String LOGIN = "user";
    private static final String PASSWORD = "0000";

    @Autowired
    public TestRestTemplate restTemplate;

    private LoginRequest loginRequest;

    @Container
    public static MySQLContainer<DBTestContainer> mySQLContainer = DBTestContainer.getInstance();

    @Container
    public static GenericContainer<?> cloudworkContainer =
            new GenericContainer<>("cloudwork")
                    .withExposedPorts(PORT)
                    .dependsOn(mySQLContainer);

    @BeforeEach
    public void setup() {
        loginRequest = new LoginRequest(LOGIN, PASSWORD);
    }

    @Test
    void loginAppTest() {
        String getLoginURI = "https://%s:%d/login".formatted(cloudworkContainer.getHost(), PORT);

        String authToken = restTemplate.postForObject(getLoginURI, loginRequest, String.class);

        System.out.println(authToken);

        Assertions.assertNotNull(authToken);
    }




}
