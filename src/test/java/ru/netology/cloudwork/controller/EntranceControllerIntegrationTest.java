package ru.netology.cloudwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudwork.dto.LoginResponse;
import ru.netology.cloudwork.service.CloudworkAuthorizationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cloudwork.TestData.LOGIN_REQUEST;

@AutoConfigureMockMvc
@WebMvcTest
public class EntranceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudworkAuthorizationService authorizationService;


    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void success_login() throws Exception {

        String TOKEN = "new token";
        when(authorizationService.initializeSession(LOGIN_REQUEST))
                .thenReturn(new LoginResponse(TOKEN));
//        when(userManager.loadUserByUsername(LOGIN_REQUEST.getLogin()))
//                .thenReturn(new UserInfo(TEST_USER));

        mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(LOGIN_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").value(TOKEN))
                .andReturn();

        verify(authorizationService).initializeSession(LOGIN_REQUEST);
    }


}
