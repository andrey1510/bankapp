package com.frontservice.clients;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.LoginPasswordDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountsClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountsClient accountsClient;

    private final String usersUrl = "http://account-service";
    private final String testLogin = "testUser";
    private final String testPassword = "password123";
    private final LoginPasswordDto loginPasswordDto = new LoginPasswordDto(testLogin, testPassword);

    @BeforeEach
    void setUp() {
        accountsClient.usersUrl = usersUrl;
    }

    @Test
    void sendAuthRequest_ShouldCallLoginEndpoint() {
        accountsClient.sendAuthRequest(loginPasswordDto);

        verify(restTemplate).postForEntity(
            eq(usersUrl + "/users/login"),
            eq(loginPasswordDto),
            eq(Void.class)
        );
    }

    @Test
    void getUserInfoDto_ShouldCallUserInfoEndpoint() {
        UserInfoDto expectedResponse = new UserInfoDto(
            testLogin,
            "Test User",
            LocalDate.of(1990, 1, 1),
            "test@example.com"
        );

        when(restTemplate.getForEntity(
            eq(usersUrl + "/users/user-info?login={login}"),
            eq(UserInfoDto.class),
            eq(testLogin)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        UserInfoDto result = accountsClient.getUserInfoDto(testLogin);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllUsersInfoExceptCurrentDto_ShouldCallUsersExceptCurrent() {
        AllUsersInfoExceptCurrentDto expectedResponse = new AllUsersInfoExceptCurrentDto(List.of());

        when(restTemplate.getForEntity(
            eq(usersUrl + "/users/users-except-current?login={login}"),
            eq(AllUsersInfoExceptCurrentDto.class),
            eq(testLogin))
        ).thenReturn(ResponseEntity.ok(expectedResponse));

        AllUsersInfoExceptCurrentDto result = accountsClient.getAllUsersInfoExceptCurrentDto(testLogin);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getUserAccountsDto_ShouldCallAccountsInfoEndpoint() {
        UserAccountsDto expectedResponse = new UserAccountsDto(
            testLogin,
            "Test User",
            "test@example.com",
            List.of()
        );

        when(restTemplate.getForEntity(
            eq(usersUrl + "/users/accounts-info?login={login}"),
            eq(UserAccountsDto.class),
            eq(testLogin))
        ).thenReturn(ResponseEntity.ok(expectedResponse));

        UserAccountsDto result = accountsClient.getUserAccountsDto(testLogin);

        assertEquals(expectedResponse, result);
    }

    @Test
    void sendUserUpdateRequest_ShouldCallEditUserEndpoint() {
        UserUpdateDto updateDto = new UserUpdateDto(
            testLogin,
            "Updated Name",
            LocalDate.of(1990, 1, 1),
            "updated@example.com"
        );

        accountsClient.sendUserUpdateRequest(updateDto);

        verify(restTemplate).postForEntity(
            eq(usersUrl + "/users/edit-user"),
            eq(updateDto),
            eq(Void.class)
        );
    }

    @Test
    void sendAccountsUpdateRequest_ShouldCallEditAccountsEndpoint() {
        List<AccountInfoDto> accounts = List.of(
            new AccountInfoDto(
                1L,
                "Main Account",
                "USD",
                new BigDecimal("1000.00"),
                true
            )
        );

        accountsClient.sendAccountsUpdateRequest(testLogin, accounts);

        verify(restTemplate).postForEntity(
            eq(usersUrl + "/users/edit-accounts"),
            any(UserAccountsDto.class),
            eq(Void.class)
        );
    }

    @Test
    void sendPasswordChangeRequest_ShouldCallChangePasswordEndpoint() {
        accountsClient.sendPasswordChangeRequest("newPassword123", testLogin);

        verify(restTemplate).postForEntity(
            eq(usersUrl + "/users/change-password"),
            any(LoginPasswordDto.class),
            eq(Void.class)
        );
    }

    @Test
    void sendSignupRequest_ShouldCallSignupEndpoint() {
        UserDto userDto = new UserDto(
            testLogin,
            "Test User",
            "Usename",
            LocalDate.of(1990, 1, 1),
            "test@example.com"
        );

        accountsClient.sendSignupRequest(userDto);

        verify(restTemplate).postForEntity(
            eq(usersUrl + "/users/signup"),
            eq(userDto),
            eq(Void.class)
        );
    }
}