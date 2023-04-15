package ru.netology.cloudwork;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@Slf4j
public class CloudworkApplication {

    public static void main(String[] args) {
        log.info("The CloudWork starting... {}", new Date());
        SpringApplication.run(CloudworkApplication.class, args);
    }

}
