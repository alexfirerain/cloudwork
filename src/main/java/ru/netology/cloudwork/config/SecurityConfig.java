package ru.netology.cloudwork.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import ru.netology.cloudwork.filter.CloudworkLogoutHandler;
import ru.netology.cloudwork.filter.ExceptionHandlerFilter;
import ru.netology.cloudwork.filter.TokenFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenFilter tokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final CloudworkLogoutHandler cloudworkLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
                    .disable()
                .cors()
                    .and()
                .csrf()
                    .disable()
                .addFilterBefore(tokenFilter, LogoutFilter.class)
                .addFilterBefore(exceptionHandlerFilter, TokenFilter.class)
                .authorizeHttpRequests()
                    .requestMatchers("/login").permitAll()
                    .anyRequest().authenticated()
                        .and()
                .logout()
//                .disable()
//                .clearAuthentication(true)
                    .addLogoutHandler(cloudworkLogoutHandler)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
//                .permitAll()
//                .logoutSuccessHandler(cloudworkLogoutHandler)
//                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
//                    userService.terminateSession(authentication.getName());
//                    SecurityContextHolder.clearContext();
//                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//                })
                .and()
                .build();
    }



}