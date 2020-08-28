package com.tpssoft.hham.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpssoft.hham.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SessionServiceTest {
    @Autowired
    private MockMvc mockMvc;
    private final String loginPath = "/auth/login";

    @Test
    @DisplayName("/auth/login return 405 Method Not Allowed if the method is not POST")
    void loginDoesNotAcceptMethodsOtherThanPost() throws Exception {
        mockMvc
                .perform(get(loginPath))
                .andExpect(status().isMethodNotAllowed());
        mockMvc
                .perform(put(loginPath))
                .andExpect(status().isMethodNotAllowed());
        mockMvc
                .perform(patch(loginPath))
                .andExpect(status().isMethodNotAllowed());
        mockMvc
                .perform(delete(loginPath))
                .andExpect(status().isMethodNotAllowed());
    }

    private MockHttpServletRequestBuilder postWithType(String path, String contentType) {
        return post(path).contentType(contentType);
    }

    @Test
    @DisplayName("/auth/login only accepts POST with json as content type")
    void loginAcceptsJsonPost() throws Exception {
        var objectMapper = new ObjectMapper();
        var body = objectMapper.writeValueAsString(new LoginRequest("", ""));
        mockMvc
                .perform(postWithType(loginPath, "application/json").content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/auth/login does not accept other content type")
    void loginDoesNotAcceptOtherContentTypes() throws Exception {
        mockMvc
                .perform(postWithType(loginPath, "text/html"))
                .andExpect(status().isUnsupportedMediaType());
        mockMvc
                .perform(postWithType(loginPath, "text/plain"))
                .andExpect(status().isUnsupportedMediaType());
        mockMvc
                .perform(postWithType(loginPath, "application/x-www-form-urlencoded"))
                .andExpect(status().isUnsupportedMediaType());
        mockMvc
                .perform(postWithType(loginPath, "multipart/form-data"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("/auth/login requires an object")
    void loginRequiresAnObject() throws Exception {
        mockMvc
                .perform(postWithType(loginPath, "application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Missing username or password results in authentication failure")
    void missingUsernameOrPasswordResultsInAuthenticationFailure() throws Exception {
        mockMvc
                .perform(postWithType(loginPath, "application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
