package ru.netology.cloudwork.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class AuxiliaryComponents {
    /**
     * The defined URLs to be bypassed with the CORS-filter.
     */
    @Value("${application.front-url}")
    private String[] frontHosts;

    /**
     * The methods that the application expects to receive from the front.
     */
    private final String[] methods = { "POST", "GET", "PUT", "DELETE", "OPTIONS" };

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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(frontHosts));
        configuration.setAllowedMethods(List.of(methods));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
