package com.frontservice.clients;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.PasswordChangeDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsClient {

    private final RestTemplate restTemplate;

    public UserInfoDto getUserInfoDto(String login) {
        ResponseEntity<UserInfoDto> responseUser = restTemplate.getForEntity(
            "http://localhost:8881/api/users/user-info?login={login}",
            UserInfoDto.class,
            login
        );

        return responseUser.getBody();
    }

    public AllUsersInfoExceptCurrentDto getAllUsersInfoExceptCurrentDto(String login) {
        ResponseEntity<AllUsersInfoExceptCurrentDto> responseAccs = restTemplate.getForEntity(
            "http://localhost:8881/api/users/users-except-current?login={login}",
            AllUsersInfoExceptCurrentDto.class,
            login
        );
        return responseAccs.getBody();
    }

    public CurrenciesDto getCurrenciesDto() {
        ResponseEntity<CurrenciesDto> responseCurrencies = restTemplate.getForEntity(
            "http://localhost:8887/api/currencies",
            CurrenciesDto.class
        );
        return responseCurrencies.getBody();
    }

    public UserAccountsDto getUserAccountsDto(String login) {
        ResponseEntity<UserAccountsDto> responseAccs = restTemplate.getForEntity(
            "http://localhost:8881/api/users/accounts-info?login={login}",
            UserAccountsDto.class,
            login
        );
        return responseAccs.getBody();
    }

    public void sendUserUpdateRequest(UserUpdateDto dto) {
        restTemplate.postForEntity(
            "http://localhost:8881/api/users/edit-user",
            dto,
            Void.class
        );
    }

    public void sendAccountsUpdateRequest(String login, List<AccountInfoDto> updatedAccounts) {
        restTemplate.postForEntity(
            "http://localhost:8881/api/users/edit-accounts",
            new UserAccountsDto(login, null, null, updatedAccounts),
            Void.class
        );
    }

    public void sendPasswordChangeRequest(String password, String login) {
        restTemplate.postForEntity(
            "http://localhost:8881/api/users/change-password",
            new PasswordChangeDto(login, password),
            Void.class
        );
    }


}
