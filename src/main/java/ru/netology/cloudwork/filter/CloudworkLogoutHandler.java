package ru.netology.cloudwork.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloudworkLogoutHandler implements LogoutHandler {

    private final CloudworkAuthorizationService authorizationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String user = authentication.getName();
        log.debug("Logging out '{}' in proper handler.", user);

        authorizationService.terminateSession(user);
//        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_OK);
        log.info("User '{}' logged out.", user);
    }
}