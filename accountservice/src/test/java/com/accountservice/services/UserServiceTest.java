package com.accountservice.services;

import com.accountservice.dto.PasswordChangeDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.exceptions.EmailAlreadyExistsException;
import com.accountservice.exceptions.LoginAlreadyExistsException;
import com.accountservice.exceptions.NoSuchUserException;
import com.accountservice.exceptions.WrongAgeException;
import com.accountservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User user;
    private UserDto userDto;
    private PasswordChangeDto passwordChangeDto;
    private UserUpdateDto userUpdateDto;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .login("testuser")
            .password("encodedPassword")
            .name("Test User")
            .email("test@example.com")
            .birthdate(LocalDate.now().minusYears(20))
            .accounts(new ArrayList<>())
            .build();

        userDto = new UserDto(
            "newuser",
            "password",
            "New User",
            LocalDate.now().minusYears(20),
            "new@example.com"
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

        account = Account.builder()
            .id(1L)
            .title("Main Account")
            .currency("USD")
            .amount(BigDecimal.ZERO)
            .user(user)
            .build();
        user.getAccounts().add(account);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        when(userRepository.existsByLogin(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.createUser(userDto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(userDto.login(), savedUser.getLogin());
        assertEquals(userDto.name(), savedUser.getName());
        assertEquals(userDto.email(), savedUser.getEmail());
        assertEquals(userDto.birthdate(), savedUser.getBirthdate());
    }

    @Test
    void createUser_WithExistingLogin_ShouldThrowException() {
        when(userRepository.existsByLogin(any())).thenReturn(true);

        assertThrows(LoginAlreadyExistsException.class, () -> 
            userService.createUser(userDto)
        );
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByLogin(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> 
            userService.createUser(userDto)
        );
    }

    @Test
    void createUser_WithInvalidAge_ShouldThrowException() {
        userDto = new UserDto(
            "newuser",
            "password",
            "New User",
            LocalDate.now().minusYears(17),
            "new@example.com"
        );

        assertThrows(WrongAgeException.class, () -> 
            userService.createUser(userDto)
        );
    }

    @Test
    void changePassword_WithValidData_ShouldChangePassword() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.changePassword(passwordChangeDto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newEncodedPassword", savedUser.getPassword());
    }

    @Test
    void changePassword_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> 
            userService.changePassword(passwordChangeDto)
        );
    }

    @Test
    void getUserInfo_WithValidLogin_ShouldReturnUserInfo() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));

        UserInfoDto result = userService.getUserInfo("testuser");

        assertEquals(user.getLogin(), result.login());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getBirthdate(), result.birthdate());
    }

    @Test
    void getUserInfo_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> 
            userService.getUserInfo("nonexistent")
        );
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserInfoDto result = userService.updateUser("testuser", userUpdateDto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(userUpdateDto.name(), savedUser.getName());
        assertEquals(userUpdateDto.email(), savedUser.getEmail());
        assertEquals(userUpdateDto.birthdate(), savedUser.getBirthdate());
    }

    @Test
    void updateUser_WithInvalidAge_ShouldThrowException() {
        userUpdateDto = new UserUpdateDto(
            "Updated User",
            "Some name",
            LocalDate.now().minusYears(17),
            "updated@example.com"
        );

        assertThrows(WrongAgeException.class, () -> 
            userService.updateUser("testuser", userUpdateDto)
        );
    }

    @Test
    void getAccountsInfo_WithValidLogin_ShouldReturnAccountsInfo() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));

        UserAccountsDto result = userService.getAccountsInfo("testuser");

        assertEquals(user.getLogin(), result.login());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());
        assertEquals(1, result.accounts().size());
        assertEquals(account.getCurrency(), result.accounts().getFirst().currency());
    }

    @Test
    void getAccountsInfo_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> 
            userService.getAccountsInfo("nonexistent")
        );
    }
} 