package ru.netology.cloudwork.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudwork.controller.ErrorController;
import ru.netology.cloudwork.dto.ErrorDto;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    ObjectMapper objectMapper = new ObjectMapper();

    private final ErrorController errorController;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.trace("filter entered");
        if ("/login".equals(request.getRequestURI())) {
            log.trace("bypass filter because login request");
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("auth-token");
        log.trace("Token met: " + token);

        if (token == null) {
            ResponseEntity<ErrorDto> errorResponse =
                    errorController
                            .handleBadRequest(
                                    new AuthenticationCredentialsNotFoundException(
                                            "Жетон не обнаружен в запросе."));
            response.setStatus(errorResponse.getStatusCode().value());
            ErrorDto body = errorResponse.getBody();
            response.getOutputStream().println(objectMapper.writeValueAsString(body));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
