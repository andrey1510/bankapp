package com.frontservice.clients;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.LoginPasswordDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsClient {

    @Value("${accountservice.url.users}")
    private String usersUrl;

    @Qualifier("accountRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<Void> sendAuthRequest(LoginPasswordDto dto) {
        return restTemplate.postForEntity(
            usersUrl + "/login",
            dto,
            Void.class
        );
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public UserInfoDto getUserInfoDto(String login) {
        return restTemplate.getForEntity(
            usersUrl + "/user-info?login={login}",
            UserInfoDto.class,
            login
        ).getBody();
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public AllUsersInfoExceptCurrentDto getAllUsersInfoExceptCurrentDto(String login) {
        return restTemplate.getForEntity(
            usersUrl + "/users-except-current?login={login}",
            AllUsersInfoExceptCurrentDto.class,
            login
        ).getBody();
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public UserAccountsDto getUserAccountsDto(String login) {
        return restTemplate.getForEntity(
            usersUrl + "/accounts-info?login={login}",
            UserAccountsDto.class,
            login
        ).getBody();
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendUserUpdateRequest(UserUpdateDto dto) {
        restTemplate.postForEntity(
            usersUrl + "/edit-user",
            dto,
            Void.class
        );
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendAccountsUpdateRequest(String login, List<AccountInfoDto> updatedAccounts) {
        restTemplate.postForEntity(
            usersUrl + "/edit-accounts",
            new UserAccountsDto(login, null, null, updatedAccounts),
            Void.class
        );
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public void sendPasswordChangeRequest(String password, String login) {
        restTemplate.postForEntity(
            usersUrl + "/change-password",
            new LoginPasswordDto(login, password),
            Void.class
        );
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public void sendSignupRequest(UserDto dto) {
        restTemplate.postForEntity(
            usersUrl + "/signup",
            dto,
            Void.class
        );
    }

}
