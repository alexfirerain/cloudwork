package ru.netology.cloudwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
//                .httpBasic().disable()
                .cors(httpSecurityCorsConfigurer -> {
                    CorsRegistry registry = new CorsRegistry();
                    registry.addMapping("/**")
                            .allowCredentials(true)
                            .allowedOrigins("http://localhost:8080")
                            .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS");
                })
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/login")
                .permitAll()
                .requestMatchers("/register")
                .hasRole("SUPERUSER")
                .anyRequest()
                .authenticated()

                .and()
                .build();

    }

}
