package ru.netology.cloudwork.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AddlConfig {

    @Value("${application.token-header}")
    private String TOKEN_HEADER;

    @Value("${application.token-prefix}")
    private String TOKEN_PREFIX;
    @Bean
    @Qualifier("header")
    public String getTokenHeaderFromConfig() {
        return TOKEN_HEADER;
    }

    @Bean
    @Qualifier("prefix")
    public String getTokenPrefixFromConfig() {
        return TOKEN_PREFIX;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
