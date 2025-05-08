package com.accountservice.services;

import com.accountservice.dto.UserDTO;
import com.accountservice.entities.User;

public interface UserService {
    User createAccount(UserDTO dto);

    User updateAccount(Long id, UserDTO dto);
}
