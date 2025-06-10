package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.dto.UserDto;
import com.frontservice.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthController authController;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private LocalDate testDate;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        testDate = LocalDate.now().minusYears(20);

        SecurityContextHolder.setContext(securityContext);

        testUserDto = new UserDto(
            "testuser",
            "password",
            "Test User",
            LocalDate.now().minusYears(20),
            "test@example.com"
        );
    }

    @Test
    void showLoginForm_WithError_ShouldAddErrorToModel() {
        String view = authController.showLoginForm("error", model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Неверный логин или пароль");
    }

    @Test
    void showLoginForm_WithoutError_ShouldNotAddErrorToModel() {
        String view = authController.showLoginForm(null, model);

        assertEquals("login", view);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void logout_ShouldLogoutAndRedirect() {
        assertEquals("redirect:/login", authController.logout(request, response));
    }

    @Test
    void showSignupForm_ShouldAddDefaultAttributes() {
        String view = authController.showSignupForm(model);

        assertEquals("signup", view);
        verify(model).addAttribute("login", "");
        verify(model).addAttribute("name", "");
        verify(model).addAttribute("birthdate", null);
        verify(model).addAttribute("email", "");
    }

    @Test
    void handleSignup_WithValidData_ShouldRedirectToLogin() {
        doNothing().when(accountsClient).sendSignupRequest(any(UserDto.class));

        String view = authController.handleSignup(
            testUserDto.login(),
            testUserDto.password(),
            testUserDto.password(),
            testUserDto.name(),
            testUserDto.birthdate(),
            testUserDto.email(),
            model,
            redirectAttributes
        );

        assertEquals("redirect:/login", view);
        verify(accountsClient).sendSignupRequest(any(UserDto.class));
        verify(redirectAttributes).addFlashAttribute("success", "Регистрация прошла успешно!");
    }

    @Test
    void handleSignup_WithMismatchedPasswords_ShouldReturnToSignup() {
        String view = authController.handleSignup(
            testUserDto.login(),
            testUserDto.password(),
            "different_password",
            testUserDto.name(),
            testUserDto.birthdate(),
            testUserDto.email(),
            model,
            redirectAttributes
        );

        assertEquals("signup", view);
        verify(model).addAttribute("errors", List.of("Пароли не совпадают"));
        verify(model).addAttribute("login", testUserDto.login());
        verify(model).addAttribute("name", testUserDto.name());
        verify(model).addAttribute("birthdate", testUserDto.birthdate());
        verify(model).addAttribute("email", testUserDto.email());
        verify(accountsClient, never()).sendSignupRequest(any());
    }

    @Test
    void handleSignup_WithClientError_ShouldReturnToSignup() {
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "User already exists".getBytes(), null))
            .when(accountsClient).sendSignupRequest(any(UserDto.class));

        String view = authController.handleSignup(
            testUserDto.login(),
            testUserDto.password(),
            testUserDto.password(),
            testUserDto.name(),
            testUserDto.birthdate(),
            testUserDto.email(),
            model,
            redirectAttributes
        );

        assertEquals("signup", view);
        verify(model).addAttribute("errors", List.of("User already exists"));
        verify(model).addAttribute("login", testUserDto.login());
        verify(model).addAttribute("name", testUserDto.name());
        verify(model).addAttribute("birthdate", testUserDto.birthdate());
        verify(model).addAttribute("email", testUserDto.email());
    }

    @Test
    void changePassword_WithValidData_ShouldRedirectToMain() {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        doNothing().when(accountsClient).sendPasswordChangeRequest(anyString(), anyString());

        String view = authController.changePassword("newpassword", "newpassword", redirectAttributes);

        assertEquals("redirect:/main", view);
        verify(accountsClient).sendPasswordChangeRequest("newpassword", "testuser");
        verify(redirectAttributes).addFlashAttribute("passwordChangeSuccess", "Пароль изменен");
    }

    @Test
    void changePassword_WithMismatchedPasswords_ShouldReturnError() {
        String view = authController.changePassword("newpassword", "different_password", redirectAttributes);

        assertEquals("redirect:/main", view);
        verify(redirectAttributes).addFlashAttribute("passwordErrors", List.of("Пароли должны совпадать"));
        verify(accountsClient, never()).sendPasswordChangeRequest(anyString(), anyString());
    }

    @Test
    void changePassword_WithClientError_ShouldReturnError() {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid password".getBytes(), null))
            .when(accountsClient).sendPasswordChangeRequest(anyString(), anyString());

        String view = authController.changePassword("newpassword", "newpassword", redirectAttributes);

        assertEquals("redirect:/main", view);
        verify(redirectAttributes).addFlashAttribute("passwordErrors", List.of("Invalid password"));
    }
} 