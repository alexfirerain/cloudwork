package ru.netology.cloudwork.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
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
 * If handling an incoming request somewhere afterward causes an exception,
 * catches it, forms an {@link ErrorDto} and writes it to the response,
 * setting status to 401, the exception being an instance of AuthenticationException,
 * and to 500 otherwise.
 */
@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request,
                                 @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {
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

    /**
     * Converts a Java object to a JSON string using the Jackson library's ObjectMapper class.
     * @param object any object taken as input.
     * @return  a JSON-string representing the given object. Null if the input object is null.
     * @throws JsonProcessingException if there is an error in the conversion process.
     */
    private String convertObjectToJson(Object object) throws JsonProcessingException {
         return object == null ?
                 null :
                 new ObjectMapper().writeValueAsString(object);
    }


}