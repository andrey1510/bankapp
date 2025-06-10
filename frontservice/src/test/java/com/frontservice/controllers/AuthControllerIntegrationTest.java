package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.configs.TestSecurityConfig;
import com.frontservice.dto.UserDto;
import com.frontservice.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, AuthControllerIntegrationTest.TestConfig.class})
class AuthControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        public AccountsClient accountsClient() {
            return mock(AccountsClient.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountsClient accountsClient;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto(
            "testuser",
            "password",
            "Test User",
            LocalDate.now().minusYears(20),
            "test@example.com"
        );
    }

    @Test
    void showLoginForm_WithError_ShouldAddErrorToModel() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "Неверный логин или пароль"));
    }

    @Test
    void showLoginForm_WithoutError_ShouldNotAddErrorToModel() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attributeDoesNotExist("error"));
    }

    @Test
    void handleSignup_WithValidData_ShouldRedirectToLogin() throws Exception {
        doNothing().when(accountsClient).sendSignupRequest(any(UserDto.class));

        mockMvc.perform(post("/signup")
                .param("login", testUserDto.login())
                .param("password", testUserDto.password())
                .param("confirm_password", testUserDto.password())
                .param("name", testUserDto.name())
                .param("birthdate", testUserDto.birthdate().toString())
                .param("email", testUserDto.email()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(flash().attribute("success", "Регистрация прошла успешно!"));
    }

    @Test
    void handleSignup_WithMismatchedPasswords_ShouldReturnToSignup() throws Exception {
        mockMvc.perform(post("/signup")
                .param("login", testUserDto.login())
                .param("password", testUserDto.password())
                .param("confirm_password", "different_password")
                .param("name", testUserDto.name())
                .param("birthdate", testUserDto.birthdate().toString())
                .param("email", testUserDto.email()))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("errors"));
    }

    @Test
    void handleSignup_WithClientError_ShouldReturnToSignup() throws Exception {
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "User already exists".getBytes(), null))
            .when(accountsClient).sendSignupRequest(any(UserDto.class));

        mockMvc.perform(post("/signup")
                .param("login", testUserDto.login())
                .param("password", testUserDto.password())
                .param("confirm_password", testUserDto.password())
                .param("name", testUserDto.name())
                .param("birthdate", testUserDto.birthdate().toString())
                .param("email", testUserDto.email()))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePassword_WithValidData_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        doNothing().when(accountsClient).sendPasswordChangeRequest(anyString(), anyString());

        mockMvc.perform(post("/user/change-password")
                .param("password", "newpassword")
                .param("repeat", "newpassword"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attribute("passwordChangeSuccess", "Пароль изменен"));
    }

} 