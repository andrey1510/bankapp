package com.accountservice.controllers;

import com.accountservice.dto.AccountInfoDto;
import com.accountservice.dto.LoginPasswordDto;
import com.accountservice.dto.PasswordChangeDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.exceptions.EmailAlreadyExistsException;
import com.accountservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;
    private PasswordChangeDto passwordChangeDto;
    private UserUpdateDto userUpdateDto;
    private UserAccountsDto userAccountsDto;
    private LoginPasswordDto loginPasswordDto;
    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
            "testuser",
            "password",
            "Test User",
            LocalDate.now().minusYears(20),
            "test@example.com"
        );

        passwordChangeDto = new PasswordChangeDto(
            "testuser",
            "newPassword"
        );

        userUpdateDto = new UserUpdateDto(
            "Updated User",
            "Some name",
            LocalDate.now().minusYears(20),
            "updated@example.com"
        );

        user = User.builder()
            .id(1L)
            .login("testuser")
            .password("encodedPassword")
            .name("Test User")
            .email("test@example.com")
            .birthdate(LocalDate.now().minusYears(20))
            .accounts(new ArrayList<>())
            .build();

        account = Account.builder()
            .id(1L)
            .title("Main Account")
            .currency("USD")
            .amount(BigDecimal.ZERO)
            .user(user)
            .build();
        user.getAccounts().add(account);

        userAccountsDto = new UserAccountsDto(
            user.getLogin(),
            user.getName(),
            user.getEmail(),
            List.of(new AccountInfoDto(
                account.getId(), account.getTitle(), account.getCurrency(), account.getAmount(), true))
        );

        loginPasswordDto = new LoginPasswordDto(
            "testuser",
            "password"
        );

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void register_WithValidData_ShouldReturnOk() {
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userController.register(userDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).createUser(userDto);
    }

    @Test
    void register_WithBindingErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("userDto", "email", "Invalid email format")
        ));

        ResponseEntity<?> response = userController.register(userDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        verify(userService, never()).createUser(any());
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new EmailAlreadyExistsException("Почта уже существует"))
            .when(userService).createUser(any());

        ResponseEntity<?> response = userController.register(userDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
    }

    @Test
    void changePassword_WithValidData_ShouldReturnOk() {
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userController.changePassword(passwordChangeDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).changePassword(passwordChangeDto);
    }

    @Test
    void changePassword_WithBindingErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("passwordChangeDto", "password", "Password is required")
        ));

        ResponseEntity<?> response = userController.changePassword(passwordChangeDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        verify(userService, never()).changePassword(any());
    }

    @Test
    void getUserInfo_WithValidLogin_ShouldReturnUserInfo() {
        UserInfoDto expectedUserInfo = new UserInfoDto(
            user.getLogin(),
            user.getName(),
            user.getBirthdate(),
            user.getEmail()
        );
        when(userService.getUserInfo(any())).thenReturn(expectedUserInfo);

        ResponseEntity<UserInfoDto> response = userController.getUserInfo("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserInfo, response.getBody());
    }

    @Test
    void updateUser_WithValidData_ShouldReturnOk() {
        when(bindingResult.hasErrors()).thenReturn(false);
        UserInfoDto expectedUserInfo = new UserInfoDto(
            user.getLogin(),
            userUpdateDto.name(),
            userUpdateDto.birthdate(),
            userUpdateDto.email()
        );
        when(userService.updateUser(any(), any())).thenReturn(expectedUserInfo);

        ResponseEntity<?> response = userController.updateUser(userUpdateDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserInfo, response.getBody());
    }

    @Test
    void updateUser_WithBindingErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("userUpdateDto", "email", "Invalid email format")
        ));

        ResponseEntity<?> response = userController.updateUser(userUpdateDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void getAccountsInfo_WithValidLogin_ShouldReturnAccountsInfo() {
        when(userService.getAccountsInfo(any())).thenReturn(userAccountsDto);

        ResponseEntity<UserAccountsDto> response = userController.getAccountsInfo("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userAccountsDto, response.getBody());
    }

    @Test
    void updateAccounts_WithValidData_ShouldReturnOk() {
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userController.updateAccounts(userAccountsDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateAccounts(userAccountsDto);
    }

    @Test
    void updateAccounts_WithBindingErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("userAccountsDto", "accounts", "Accounts list is required")
        ));

        ResponseEntity<?> response = userController.updateAccounts(userAccountsDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        verify(userService, never()).updateAccounts(any());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnOk() {
        when(userService.authenticateUser(any(), any())).thenReturn(true);

        ResponseEntity<Void> response = userController.login(loginPasswordDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() {
        when(userService.authenticateUser(any(), any())).thenReturn(false);

        ResponseEntity<Void> response = userController.login(loginPasswordDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
} 