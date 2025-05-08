package com.accountservice.services;

import com.accountservice.dto.AccountDTO;
import com.accountservice.entities.Account;

import java.util.UUID;

public interface AccountService {
    Account createAccount(Long accountId, AccountDTO dto);

    Account updateAccount(UUID id, Long accountId, AccountDTO dto);

    void deleteAccount(UUID id, Long accountId);
}
