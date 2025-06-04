package com.accountservice.services;

import com.accountservice.dto.AccountInfoDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    User createUser(UserDto dto);

    UserInfoDto getUserInfo(String login);

    UserInfoDto updateUser(String login, UserUpdateDto dto);

    UserAccountsDto getAccountsInfo(String login);

    void updateAccounts(UserAccountsDto dto);
}
