package ru.netology.cloudwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import ru.netology.cloudwork.filter.ExceptionHandlerFilter;
import ru.netology.cloudwork.filter.TokenFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${application.front-url}")  // don't work :(
    static String[] frontHosts;

    private final TokenFilter tokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .cors(
//                    httpSecurityCorsConfigurer -> {                   // don't work :(
//                        CorsRegistry registry = new CorsRegistry();
//                        registry.addMapping("/**")
//                            .allowCredentials(true)
//                            .allowedOrigins("http://localhost:8080", "http://localhost:8081")
//                            .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS");
//                    }
                )
                .and()
                .csrf().disable()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, TokenFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and().build();
    }

}