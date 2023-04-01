package ru.netology.cloudwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import ru.netology.cloudwork.entity.Role;
import ru.netology.cloudwork.entity.UserEntity;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserEntity user = new UserEntity("user",
                                encoder.encode("0000"),
                                Role.USER);

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("9999"))
                .roles("SUPERUSER")
                .build();

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.createUser(user);
        jdbcUserDetailsManager.createUser(admin);

        return jdbcUserDetailsManager;
//        InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
