package ru.netology.cloudwork.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.netology.cloudwork.filter.ExceptionHandlerFilter;
import ru.netology.cloudwork.filter.TokenFilter;
import ru.netology.cloudwork.service.UserService;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
//@EnableWebMvc
public class SecurityConfig {
    @Value("${application.front-url}")
    private String[] frontHosts;

    /**
     * The methods that the application expects to receive from the front.
     */
    private final String[] methods = { "POST", "GET", "PUT", "DELETE", "OPTIONS" };

    private final TokenFilter tokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
//    private final UserService userService;
//    private final LogoutSuccessHandler cloudworkLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .cors(
                    httpSecurityCorsConfigurer -> {                   // don't work :(
                        CorsRegistry registry = new CorsRegistry();
                        registry.addMapping("/**")
                            .allowCredentials(true)
                            .allowedOrigins(frontHosts)
                            .allowedMethods(methods);
                    }
                )
//                .and()
                .csrf().disable()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, TokenFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .disable()
//                .clearAuthentication(false)
//                .logoutUrl("/logout")
//                .permitAll()
//                .logoutSuccessHandler(cloudworkLogoutHandler)
//                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
//                    userService.terminateSession(authentication.getName());
//                    SecurityContextHolder.clearContext();
//                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//                })
//                .and()
                .build();
    }

}