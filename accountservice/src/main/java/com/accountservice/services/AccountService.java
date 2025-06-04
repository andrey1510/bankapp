package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.AccountDTO;
import com.accountservice.entities.Account;

import java.util.UUID;

public interface AccountService {

    void updateAccountBalance(AccountBalanceChangeDto request);
}
