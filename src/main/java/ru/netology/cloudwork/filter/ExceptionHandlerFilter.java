package ru.netology.cloudwork.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudwork.dto.ErrorDto;

import java.io.IOException;

/**
 * The Filter to be placed just prior to the {@link TokenFilter} with duty
 * to form correct error responses whenever exceptions occur aside the scope
 * the {@link ru.netology.cloudwork.controller.ErrorController ErrorController} can reach.
 */
@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            log.debug("ExceptionHandlerFilter is to handle {}: {}",
                    e.getClass().getSimpleName(),
                    e.getLocalizedMessage());

            response.setStatus(e instanceof AuthenticationException ?
                    HttpStatus.UNAUTHORIZED.value() :
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setCharacterEncoding("UTF-8");

            ErrorDto errorResponse = new ErrorDto(e);
            log.info("ExceptionHandlerFilter crafted error response: {} (code {})",
                    errorResponse,
                    response.getStatus());

            response.getWriter().write(convertObjectToJson(errorResponse));
    }
}

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }


}