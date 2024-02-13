package ru.netology.cloudwork.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

/**
 * A handler to process user exit from CloudWork.
 * It is fired by Logout Filter and commands authorization service
 * to terminate the current user session.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CloudworkLogoutHandler implements LogoutHandler {

    /**
     * A server for authorization in CloudWork.
     */
    private final CloudworkAuthorizationService authorizationService;

    /**
     * The proper routine of the handler.
     * It triggers the authentication server to close a session and sets a response status to OK.
     * @param request the HTTP request.
     * @param response the HTTP response.
     * @param authentication the current principal details.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String user = authentication.getName();
        log.debug("Logging out '{}' in proper handler.", user);
        authorizationService.terminateSession(user);
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("User '{}' logged out.", user);
    }
}