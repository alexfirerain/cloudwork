package ru.netology.cloudwork.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudwork.controller.ErrorController;
import ru.netology.cloudwork.dto.ErrorDto;
import ru.netology.cloudwork.model.LoggedIn;
import ru.netology.cloudwork.model.UserInfo;
import ru.netology.cloudwork.service.IdentityService;
import ru.netology.cloudwork.service.UserManager;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    static final String TOKEN_HEADER = "auth-token";
    static final String TOKEN_PREFIX = "Bearer ";
    ObjectMapper objectMapper = new ObjectMapper();

    private final ErrorController errorController;
    private final IdentityService identityService;
    private final UserManager userManager;



    /**
     * Looks through incoming requests for tokens in their {@link #TOKEN_HEADER}.
     * When it finds no token, bypasses the request.
     * When does, validates it and sets the linked user authenticated to this request.
     * @param request   a {@link HttpServletRequest} coming to the filter.
     * @param response  a {@link HttpServletResponse} coming from the filter.
     * @param filterChain a {@link FilterChain} filtering the incoming requests.
     * @throws ServletException sometimes somehow.
     * @throws IOException  probably when physical troubles come.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        log.debug("Token in the request filtered: " + token);

        if (token != null) {
            UserInfo user = userManager.findUserByToken(token);

            try {
                if (user == null) {
                    log.warn("No mapped user, invalid token met");
                    throw new BadCredentialsException("Жетон не принадлежит активной сессии CloudWork");
                }
                log.trace("User by token found: {}", user.getUsername());
                LoggedIn auth = (LoggedIn) identityService.authenticate(new LoggedIn(user));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("User {} set authenticated", auth.getPrincipal());
            } catch (AuthenticationException e) {
                response.setStatus(401);
//                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(),
                        new ErrorDto(e.getLocalizedMessage(), ErrorController.nextErrorId()));

            }
        }

        filterChain.doFilter(request, response);
    }

    private void submitAuthErrorResponse(HttpServletResponse response, String errorMsg) throws IOException {
        log.debug("Trying to send error from filter");
        ResponseEntity<ErrorDto> errorResponse = errorController
                .handleAuthorizationFailure(new AuthenticationCredentialsNotFoundException(errorMsg));
        response.setStatus(errorResponse.getStatusCode().value());
        log.debug("Response status: {}", errorResponse.getStatusCode());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
                objectMapper.writeValueAsString(errorResponse.getBody()));
    }

    /**
     * Extracts from the request a matter of token in {@link #TOKEN_HEADER}
     * following after {@link #TOKEN_PREFIX}.
     * @param request a {@link HttpServletRequest} under the extraction.
     * @return a string from TOKEN_HEADER value succeeding after TOKEN_PREFIX
     * or {@code null} if no such header or its value doesn't start with TOKEN_PREFIX.
     */
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        return token == null || token.isBlank() || !token.startsWith(TOKEN_PREFIX) ?
                null : token.substring(TOKEN_PREFIX.length());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationFailure(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        log.warn("An Authorization exception in the filter: {}", message);
        return ResponseEntity.status(401).body(
                new ErrorDto(message, ErrorController.nextErrorId()));
    }
}
