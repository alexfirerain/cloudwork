package ru.netology.cloudwork.filter;

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

    private final ErrorController errorController;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/login".equals(request.getRequestURI()))
            return;

        String token = request.getHeader("auth-token");
        log.trace("Token met: " + token);

        if (token == null) {
            ResponseEntity<ErrorDto> errorResponse =
                    errorController
                            .handleBadRequest(
                                    new AuthenticationCredentialsNotFoundException(
                                            "Жетон не обнаружен в запросе."));
            response.setStatus(errorResponse.getStatusCode().value());
            response.getOutputStream().write(errorResponse.getBody());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
