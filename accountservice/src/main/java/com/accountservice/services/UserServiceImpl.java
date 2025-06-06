package com.accountservice.services;


import com.accountservice.dto.AccountInfoDto;
import com.accountservice.dto.AllUsersInfoExceptCurrentDto;
import com.accountservice.dto.PasswordChangeDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.exceptions.AccountWithSuchCurrencyAlreadyExists;
import com.accountservice.exceptions.NoSuchUserException;
import com.accountservice.exceptions.NotNullBalanceException;
import com.accountservice.exceptions.WrongAgeException;
import com.accountservice.exceptions.EmailAlreadyExistsException;
import com.accountservice.exceptions.LoginAlreadyExistsException;
import com.accountservice.repositories.AccountRepository;
import com.accountservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String WRONG_AGE = "Возраст должен быть 18 лет или старше";
    private static final String LOGIN_EXISTS = "Возраст должен быть 18 лет или старше";
    private static final String EMAIL_EXISTS = "Email уже занят";
    private static final String USER_NOT_FOUND = "Пользователь не найден";
    private static final String ACCOUNT_WITH_SUCH_CURRENCY_EXISTS = "Счет с такой валютой уже есть";

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {

        if (userDto.birthdate().isAfter(LocalDate.now().minusYears(18)))
            throw new WrongAgeException(WRONG_AGE);

        if (userRepository.existsByLogin(userDto.login()))
            throw new LoginAlreadyExistsException(LOGIN_EXISTS);

        if (userRepository.existsByEmail(userDto.email()))
            throw new EmailAlreadyExistsException(EMAIL_EXISTS);

        return userRepository.save(User.builder()
            .login(userDto.login())
            .password(userDto.password())
            .name(userDto.name())
            .email(userDto.email())
            .birthdate(userDto.birthdate())
            .accounts(new ArrayList<>())
            .build());
    }

    @Transactional
    @Override
    public User changePassword(PasswordChangeDto passwordChangeDto) {

        User user = userRepository.findByLogin(passwordChangeDto.login())
            .orElseThrow(() -> new NoSuchUserException(USER_NOT_FOUND));

        user.setPassword(passwordChangeDto.password());

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(String login) {

        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new NoSuchUserException(USER_NOT_FOUND));

        return new UserInfoDto(
            user.getLogin(),
            user.getName(),
            user.getBirthdate(),
            user.getEmail()
        );
    }

    @Transactional
    @Override
    public UserInfoDto updateUser(String login, UserUpdateDto userDto) {

        if (userDto.birthdate().isAfter(LocalDate.now().minusYears(18)))
            throw new WrongAgeException(WRONG_AGE);

        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new NoSuchUserException(USER_NOT_FOUND));

        user.setName(userDto.name());
        user.setBirthdate(userDto.birthdate());
        user.setEmail(userDto.email());

        userRepository.save(user);

        return getUserInfo(login);
    }

    @Transactional(readOnly = true)
    @Override
    public AllUsersInfoExceptCurrentDto getAllUsersInfoExceptCurrent(String currentUserLogin) {
        List<User> users = userRepository.findAllExceptCurrentUserWithAccounts(currentUserLogin);

        List<UserAccountsDto> userDtos = users.stream()
            .map(user -> {
                List<AccountInfoDto> accountDtos = user.getAccounts().stream()
                    .map(account -> new AccountInfoDto(
                        account.getId(),
                        account.getTitle(),
                        account.getCurrency(),
                        account.getAmount(),
                        true
                    ))
                    .collect(Collectors.toList());

                return new UserAccountsDto(
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    accountDtos
                );
            })
            .collect(Collectors.toList());

        return new AllUsersInfoExceptCurrentDto(userDtos);
    }


    @Transactional(readOnly = true)
    @Override
    public UserAccountsDto getAccountsInfo(String login) {
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new NoSuchUserException(USER_NOT_FOUND));

        return new UserAccountsDto(login, user.getName(), user.getEmail(), user.getAccounts().stream()
            .map(account -> new AccountInfoDto(
                account.getId(),
                account.getTitle(),
                account.getCurrency(),
                account.getAmount(),
                true
            ))
            .collect(Collectors.toList()));

    }

    @Transactional
    @Override
    public void updateAccounts(UserAccountsDto dto) {

        User user = userRepository.findByLogin(dto.login())
            .orElseThrow(() -> new NoSuchUserException(USER_NOT_FOUND));

        List<Account> accountsToDelete = dto.accounts().stream()
            .filter(acc -> !acc.isExisting())
            .map(AccountInfoDto::currency)
            .map(currency -> accountRepository.findByUserAndCurrency(user, currency))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .peek(account -> {
                if (account.getAmount() != null && account.getAmount() != 0.0) {
                    throw new NotNullBalanceException(
                        String.format("Нельзя удалить счет %s (валюта: %s) - ненулевой баланс",
                            account.getTitle(),
                            account.getCurrency()));
                }
            })
            .collect(Collectors.toList());

        accountRepository.deleteAll(accountsToDelete);

        List<Account> accountsToCreate = dto.accounts().stream()
            .filter(AccountInfoDto::isExisting)
            .filter(acc -> acc.accountId() == null || acc.accountId() == 0)
            .map(acc -> Account.builder()
                .amount(0.0)
                .title(acc.title())
                .currency(acc.currency())
                .user(user)
                .build())
            .collect(Collectors.toList());

        Set<String> existingCurrencies = accountRepository.findByUser(user).stream()
            .map(Account::getCurrency)
            .collect(Collectors.toSet());

        for (Account account : accountsToCreate) {
            if (existingCurrencies.contains(account.getCurrency())) {
                throw new AccountWithSuchCurrencyAlreadyExists(ACCOUNT_WITH_SUCH_CURRENCY_EXISTS);
            }
        }

        accountRepository.saveAll(accountsToCreate);
    }
}
