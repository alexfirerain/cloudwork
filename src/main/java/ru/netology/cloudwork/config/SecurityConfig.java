package ru.netology.cloudwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.netology.cloudwork.filter.ExceptionHandlerFilter;
import ru.netology.cloudwork.filter.TokenFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${application.front-url}")
    static String[] frontHosts;

    private final TokenFilter tokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .cors(
//                        httpSecurityCorsConfigurer -> {
//                    CorsRegistry registry = new CorsRegistry();
//
//                    registry.addMapping("/**")
//                            .allowCredentials(true)
//                            .allowedOrigins(frontHosts)
//                            .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS");
//                }
                )
                .and().csrf().disable()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, TokenFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and().build();

    }

//    @Bean
//    public AuthenticationManager authenticationManager(UserManager userService,
//                                                       PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return new ProviderManager(authProvider);
//    }




}
