//package ru.netology.cloudwork.filter;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
//import org.springframework.stereotype.Component;
//import ru.netology.cloudwork.service.UserService;
//
//import java.io.IOException;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CloudworkLogoutHandler extends
//        SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
//
//    private final UserService userService;
//
//    @Override
//    public void onLogoutSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication)
//            throws IOException, ServletException {
//
//        log.debug("LogoutHandler triggered with {}", authentication);
//        String token = request.getHeader("auth-token");
//        log.debug("is {}", token);
//
//        userService.terminateSession(authentication.getName());
//
//        super.onLogoutSuccess(request, response, authentication);
//    }
//}