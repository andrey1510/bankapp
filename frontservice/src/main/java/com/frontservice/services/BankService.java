package com.frontservice.services;

import com.frontservice.clients.ExchangeClient;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AccountUserInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.UserAccountsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankService {

    private final ExchangeClient exchangeClient;

    public List<AccountInfoDto> combineCurrencies(
        UserAccountsDto userAccountsDto,
        CurrenciesDto currenciesDto) {

        List<AccountInfoDto> allAccounts = new ArrayList<>(userAccountsDto.accounts());

        Set<String> existingCurrencies = userAccountsDto.accounts().stream()
            .map(AccountInfoDto::currency)
            .collect(Collectors.toSet());

        currenciesDto.currencies().forEach((currencyName, currencyTitle) -> {
            if (!existingCurrencies.contains(currencyName)) {
                allAccounts.add(new AccountInfoDto(
                    0L,
                    currencyTitle,
                    currencyName,
                    0.0,
                    false
                ));
            }
        });

        return allAccounts;
    }

    public List<AccountUserInfoDto> convertToAccountUserInfoList(UserAccountsDto userAccountsDto) {
        return userAccountsDto.accounts().stream()
            .map(account -> new AccountUserInfoDto(
                userAccountsDto.name(),
                account.accountId(),
                userAccountsDto.email(),
                account.title(),
                account.currency(),
                account.amount()
            ))
            .collect(Collectors.toList());
    }

    public List<AccountUserInfoDto> convertAllUsersToAccountInfo(AllUsersInfoExceptCurrentDto allUsersDto) {
        if (allUsersDto == null || allUsersDto.users() == null)
            return List.of();

        return allUsersDto.users().stream()
            .flatMap(userDto -> convertToAccountUserInfoList(userDto).stream())
            .collect(Collectors.toList());
    }

    public Optional<AccountInfoDto> findAccountById(UserAccountsDto accountsDto, Long accountId) {
        return accountsDto.accounts().stream()
            .filter(acc -> acc.accountId().equals(accountId))
            .findFirst();
    }

    public Optional<AccountInfoDto> findAccountById(List<UserAccountsDto> users, Long targetAccountId) {
        return users.stream()
            .flatMap(user -> user.accounts().stream())
            .filter(account -> account.accountId().equals(targetAccountId))
            .findFirst();
    }

}
