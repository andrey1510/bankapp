package com.accountservice.services;


import com.accountservice.dto.AccountInfoDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.repositories.AccountRepository;
import com.accountservice.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {

        if (userDto.birthdate().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Возраст должен быть 18 лет или старше");
        }

        if (userRepository.existsByLogin(userDto.login())) {
            throw new IllegalArgumentException("Логин уже занят");
        }
        if (userRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        //ToDo
        List<Account> accounts = List.of(
            Account.builder().amount(0.0).title("Рубль").currency("RUR").isEnabled(false).build(),
            Account.builder().amount(0.0).title("Доллар").currency("USD").isEnabled(false).build(),
            Account.builder().amount(0.0).title("Юань").currency("CNY").isEnabled(false).build()
        );

        User user = User.builder()
            .login(userDto.login())
            .password(userDto.password())
            .name(userDto.name())
            .email(userDto.email())
            .birthdate(userDto.birthdate())
            .accounts(new ArrayList<>())
            .build();
        accounts.forEach(user::addAccount);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(String login) {
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return new UserInfoDto(
            user.getLogin(),
            user.getName(),
            user.getBirthdate(),
            user.getEmail()
        );
    }

    @Transactional
    @Override
    public UserInfoDto updateUser(String login, UserUpdateDto dto) {
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        user.setName(dto.name());
        user.setBirthdate(dto.birthdate());
        user.setEmail(dto.email());
        userRepository.save(user);

        return getUserInfo(login);
    }



    @Transactional(readOnly = true)
    @Override
    public UserAccountsDto getAccountsInfo(String login) {
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return new UserAccountsDto(login, user.getEmail(), user.getAccounts().stream()
            .map(account -> new AccountInfoDto(
                account.getId(),
                account.getTitle(),
                account.getCurrency(),
                account.getAmount(),
                account.getIsEnabled()
            ))
            .collect(Collectors.toList()));

    }


    @Transactional
    @Override
    public void updateAccounts(UserAccountsDto dto) {
        List<Long> accountIds = dto.accounts().stream()
            .map(AccountInfoDto::accountId)
            .collect(Collectors.toList());

        List<Account> accounts = accountRepository.findAllById(accountIds);

        Map<Long, AccountInfoDto> dtoMap = dto.accounts().stream()
            .collect(Collectors.toMap(AccountInfoDto::accountId, Function.identity()));

        accounts.forEach(account -> {
            AccountInfoDto accountDto = dtoMap.get(account.getId());

            account.setIsEnabled(accountDto.isEnabled());

            if (!accountDto.isEnabled()) {
                account.setAmount(0.0);
            } else {
                account.setAmount(accountDto.amount());
            }
        });

        accountRepository.saveAll(accounts);
    }
}
