package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.dto.ExchangeRate;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final AuthService authService;

    private final AccountsClient accountsClient;
    private final ExchangeClient exchangeClient;

    private final BankService bankService;

    @GetMapping("/main")
    @PreAuthorize("hasRole('USER')")
    public String dashboard(Model model) {

        UserInfoDto userInfo = accountsClient.getUserInfoDto(authService.getLoginFromSecurityContext());

        model.addAttribute("ratesEndpoint", "/api/rates");
        model.addAttribute("login", userInfo.login());
        model.addAttribute("name", userInfo.name());
        model.addAttribute("birthdate", userInfo.birthdate());
        model.addAttribute("email", userInfo.email());

        try {
            UserAccountsDto currentAccounts = accountsClient.getUserAccountsDto(authService.getLoginFromSecurityContext());

            model.addAttribute("transferAccounts",
                bankService.convertToAccountUserInfoList(currentAccounts));
            model.addAttribute("accounts",
                bankService.combineCurrencies(currentAccounts, exchangeClient.getCurrenciesDto()));
            model.addAttribute("transferOtherAccounts",
                bankService.convertAllUsersToAccountInfo(
                    accountsClient.getAllUsersInfoExceptCurrentDto(authService.getLoginFromSecurityContext())));

        } catch (HttpClientErrorException e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
        }

        return "main";
    }

    @GetMapping("/api/rates")
    @ResponseBody
    public List<ExchangeRate> getExchangeRates() {
        return exchangeClient.getRates().rates();
    }

}