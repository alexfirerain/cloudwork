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
import ru.netology.cloudwork.model.LoggedIn;
import ru.netology.cloudwork.service.IdentityService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    static final String TOKEN_HEADER = "token-auth";
    static final String TOKEN_PREFIX = "bearer ";
    ObjectMapper objectMapper = new ObjectMapper();

    private final ErrorController errorController;
    private final IdentityService identityService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        log.trace("Token in the request filtered: " + token);

//        if (token == null) {
//            submitErrorResponse(response, "Жетон не обнаружен в заголовках.");
//            return;
//        } else if (!identityService.validateToken(token)) {
//            submitErrorResponse(response, "Жетон не действителен");
//            return;
//        }

        LoggedIn auth = new LoggedIn();



        filterChain.doFilter(request, response);
    }

    private void submitErrorResponse(HttpServletResponse response, String errorMsg) throws IOException {
        ResponseEntity<ErrorDto> errorResponse = errorController
                .handleBadRequest(new AuthenticationCredentialsNotFoundException(errorMsg));
        response.setStatus(errorResponse.getStatusCode().value());
        ErrorDto body = errorResponse.getBody();
        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN_HEADER);

        if (token != null && !token.isBlank() && token.startsWith(TOKEN_PREFIX))
            token = token.substring(TOKEN_PREFIX.length());

        return token;
    }
}
