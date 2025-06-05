package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;

public interface AccountService {

    void updateBalanceCash(AccountBalanceChangeDto request);

    void updateBalanceTransfer(BalanceUpdateRequestDto request);
}
