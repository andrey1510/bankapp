package com.frontservice.clients;

import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MainClient {

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
}
