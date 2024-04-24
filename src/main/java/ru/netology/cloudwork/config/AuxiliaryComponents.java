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

/**
 * The additional beans and required constants
 * for the CloudWork to work.
 */
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
    
    /**
     * The name of header that a front-app will be sending a token in.
     */
    @Value("${application.token-header}")
    private String TOKEN_HEADER;

    /**
     * A prefix that precedes the token itself in a header string.
     */
    @Value("${application.token-prefix}")
    private String TOKEN_PREFIX;

    /**
     * A bean for usage of header name at beans' initialization.
     * @return  a string from properties file to be used as a token header in requests.
     */
    @Bean
    @Qualifier("header")
    public String getTokenHeaderFromConfig() {
        return TOKEN_HEADER;
    }

    /**
     * A bean for usage of header prefix at beans' initialization.
     * @return  a string from properties used as a prefix in token header in requests.
     */
    @Bean
    @Qualifier("prefix")
    public String getTokenPrefixFromConfig() {
        return TOKEN_PREFIX;
    }

    /**
     * An encoder for secure password storing.
     * @return  a {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides configuration settings for CORS filter.
     * @return  a bean of type {@link CorsConfigurationSource}.
     */
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
