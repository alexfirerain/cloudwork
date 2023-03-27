package ru.netology.cloudwork.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/register").hasRole("SUPERUSER")
                .anyRequest().authenticated()

                .and().httpBasic().and().build();

    }

}
