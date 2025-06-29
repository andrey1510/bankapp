package com.frontservice.services;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AccountUserInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.UserAccountsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

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
                    BigDecimal.ZERO,
                    false
                ));
            }
        });
        log.info("combineCurrencies: {}", allAccounts);

        return allAccounts;
    }

    public List<AccountUserInfoDto> convertToAccountUserInfoList(UserAccountsDto userAccountsDto) {
        List<AccountUserInfoDto> collect = userAccountsDto.accounts().stream()
            .map(account -> new AccountUserInfoDto(
                userAccountsDto.name(),
                account.accountId(),
                userAccountsDto.email(),
                account.title(),
                account.currency(),
                account.amount()
            ))
            .collect(Collectors.toList());
        log.info("convertToAccountUserInfoList: {}", collect);

        return collect;
    }

    public List<AccountUserInfoDto> convertAllUsersToAccountInfo(AllUsersInfoExceptCurrentDto allUsersDto) {
        if (allUsersDto == null || allUsersDto.users() == null)
            return List.of();

        List<AccountUserInfoDto> collect = allUsersDto.users().stream()
            .flatMap(userDto -> convertToAccountUserInfoList(userDto).stream())
            .collect(Collectors.toList());
        log.info("convertAllUsersToAccountInfo: {}", collect);

        return collect;
    }

    public Optional<AccountInfoDto> findAccountById(UserAccountsDto accountsDto, Long accountId) {
        Optional<AccountInfoDto> account = accountsDto.accounts().stream()
            .filter(acc -> acc.accountId().equals(accountId))
            .findFirst();
        log.info("findAccountById: {}", account);
        return account;
    }

    public Optional<AccountInfoDto> findAccountById(List<UserAccountsDto> users, Long targetAccountId) {
        Optional<AccountInfoDto> acc = users.stream()
            .flatMap(user -> user.accounts().stream())
            .filter(account -> account.accountId().equals(targetAccountId))
            .findFirst();
        log.info("findAccountById: {}", acc);
        return acc;
    }

    public Optional<String> findLoginByAccountId(List<UserAccountsDto> users, Long accountId) {
        Optional<String> login = users.stream()
            .filter(user -> user.accounts().stream()
                .anyMatch(account -> account.accountId().equals(accountId)))
            .findFirst()
            .map(UserAccountsDto::login);
        log.info("findLoginByAccountId: {}", login);
        return login;
    }

}
