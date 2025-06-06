package com.accountservice.services;

import com.accountservice.dto.AllUsersInfoExceptCurrentDto;
import com.accountservice.dto.PasswordChangeDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.entities.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto dto);

    User changePassword(PasswordChangeDto passwordChangeDto);

    UserInfoDto getUserInfo(String login);

    UserInfoDto updateUser(String login, UserUpdateDto dto);

    AllUsersInfoExceptCurrentDto getAllUsersInfoExceptCurrent(String currentUserLogin);

    UserAccountsDto getAccountsInfo(String login);

    void updateAccounts(UserAccountsDto dto);
}
