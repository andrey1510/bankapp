package com.frontservice.clients;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.LoginPasswordDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsClient {

    @Value("${accountservice.url}")
    protected String usersUrl;

    @Qualifier("accountRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<Void> sendAuthRequest(LoginPasswordDto dto) {
        ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity(
            String.format("%s/users/login", usersUrl),
            dto,
            Void.class
        );
        log.info("Send auth request to " + usersUrl);
        return voidResponseEntity;
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public UserInfoDto getUserInfoDto(String login) {
        UserInfoDto user = restTemplate.getForEntity(
            String.format("%s/users/user-info?login={login}", usersUrl),
            UserInfoDto.class,
            login
        ).getBody();
        log.info("Get user info from " + usersUrl);
        return user;
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public AllUsersInfoExceptCurrentDto getAllUsersInfoExceptCurrentDto(String login) {
        AllUsersInfoExceptCurrentDto allUsers = restTemplate.getForEntity(
            String.format("%s/users/users-except-current?login={login}", usersUrl),
            AllUsersInfoExceptCurrentDto.class,
            login
        ).getBody();
        log.info("Get all users info from " + usersUrl);
        return allUsers;
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public UserAccountsDto getUserAccountsDto(String login) {
        UserAccountsDto user = restTemplate.getForEntity(
            String.format("%s/users/accounts-info?login={login}", usersUrl),
            UserAccountsDto.class,
            login
        ).getBody();
        log.info("Get user accounts from " + usersUrl);
        return user;
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendUserUpdateRequest(UserUpdateDto dto) {
        restTemplate.postForEntity(
            String.format("%s/users/edit-user", usersUrl),
            dto,
            Void.class
        );
        log.info("Send user update request to " + usersUrl);
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendAccountsUpdateRequest(String login, List<AccountInfoDto> updatedAccounts) {
        restTemplate.postForEntity(
            String.format("%s/users/edit-accounts", usersUrl),
            new UserAccountsDto(login, null, null, updatedAccounts),
            Void.class
        );
        log.info("Send account update request to " + usersUrl);
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public void sendPasswordChangeRequest(String password, String login) {
        restTemplate.postForEntity(
            String.format("%s/users/change-password", usersUrl),
            new LoginPasswordDto(login, password),
            Void.class
        );
        log.info("Send password change request to " + usersUrl);
    }

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 1, backoff = @Backoff(delay = 1000)
    )
    public void sendSignupRequest(UserDto dto) {
        restTemplate.postForEntity(
            String.format("%s/users/signup", usersUrl),
            dto,
            Void.class
        );
        log.info("Send signup request to " + usersUrl);
    }

}
