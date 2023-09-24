package ru.netology.cloudwork.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudwork.model.LoggedIn;
import ru.netology.cloudwork.model.UserInfo;
import ru.netology.cloudwork.service.AuthChecker;
import ru.netology.cloudwork.service.UserManager;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    /**
     * A name for the header which will carry token
     * from the front app.
     */
    private static String TOKEN_HEADER;

    /**
     * A prefix which will precede the token body
     * in the token-header string.
     */
    private static String TOKEN_PREFIX;

    /**
     * A custom {@link AuthenticationManager
     * AuthenticationManager} implementation in the CloudWork.
     */
    private final AuthChecker authChecker;
    /**
     * A custom {@link UserDetailsService
     * UserDetailsService} implementation in the CloudWork.
     */
    private final UserManager userManager;

    @Autowired
    public void setTokenHeader(@Qualifier("header") String tokenHeader) {
        TOKEN_HEADER = tokenHeader;
        log.debug("Authentication header set as '{}'", TOKEN_HEADER);
    }

    @Autowired
    public void setTokenPrefix(@Qualifier("prefix") String tokenPrefix) {
        TOKEN_PREFIX = tokenPrefix;
        log.debug("Token prefix defined as '{}'", TOKEN_PREFIX);
    }



    /**
     * Looks through incoming requests for tokens in their {@link #TOKEN_HEADER}.
     * When it finds no token, bypasses the request.
     * When does, validates it and sets the linked user authenticated to this request-thread.
     *
     * @param request     a {@link HttpServletRequest} coming to the filter.
     * @param response    a {@link HttpServletResponse} coming from the filter.
     * @param filterChain a {@link FilterChain} filtering the incoming requests.
     * @throws ServletException sometimes somehow.
     * @throws IOException      probably when physical troubles come.
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        log.debug("Token in the request filtered: " + token);

        boolean toBeAuthenticated = !"/login".equals(request.getRequestURI());


        if (toBeAuthenticated && token != null) {
            UserInfo user = userManager.findUserByToken(token);

            if (user == null) {
                log.warn("No mapped user, invalid token met");
                throw new BadCredentialsException("Жетон не принадлежит активной сессии CloudWork");
            }
            log.trace("User by token found: {}", user.getUsername());
            LoggedIn auth = (LoggedIn) authChecker.authenticate(new LoggedIn(user));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("User '{}' got authenticated for the request", auth.getPrincipal());
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Extracts from the request a matter of token in {@link #TOKEN_HEADER}
     * following after {@link #TOKEN_PREFIX}.
     *
     * @param request a {@link HttpServletRequest} under the extraction.
     * @return a string from TOKEN_HEADER value succeeding after TOKEN_PREFIX
     * or {@code null} if no such header or its value doesn't start with TOKEN_PREFIX.
     */
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        return token == null || token.isBlank() || !token.startsWith(TOKEN_PREFIX) ?
                null : token.substring(TOKEN_PREFIX.length());
    }


}
