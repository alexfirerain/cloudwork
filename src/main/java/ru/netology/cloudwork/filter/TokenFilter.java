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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

import java.io.IOException;

/**
 * A filter looking through the requests whether they contain
 * a proper token in a special header. If the request really
 * holds a token being assigned to some user, this gets authenticated
 * for him or her. Otherwise, rejects the request.
 */
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
     * A server for authorization in CloudWork.
     */
    private final CloudworkAuthorizationService authorizationService;

    /**
     * An initializer for a static value of header's name
     * that carries token for request's authorization.
     * @param tokenHeader a header's name.
     */
    @Autowired
    public void setTokenHeader(@Qualifier("header") String tokenHeader) {
        TOKEN_HEADER = tokenHeader;
        log.debug("Authentication header set as '{}'", TOKEN_HEADER);
    }

    /**
     * An initializer for a static value of prefix
     * that the authorization token starts with.
     * @param tokenPrefix a token value's prefix.
     */
    @Autowired
    public void setTokenPrefix(@Qualifier("prefix") String tokenPrefix) {
        TOKEN_PREFIX = tokenPrefix;
        log.debug("Token prefix defined as '{}'", TOKEN_PREFIX);
    }

    /**
     * The proper routine of the filter. It looks through incoming requests
     * for tokens in their {@link #TOKEN_HEADER}.
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
        if ("/login".equals(request.getServletPath())) {
            log.debug("Bypassing request to \"/login\" endpoint");
        } else {
            String token = extractToken(request);
            log.debug("Token in the request filtered: " + token);
            authorizationService.authenticateByToken(token);
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
