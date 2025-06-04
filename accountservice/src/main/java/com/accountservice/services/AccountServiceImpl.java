package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.entities.Account;
import com.accountservice.exceptions.AccountIsDisabledException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public void updateAccountBalance(AccountBalanceChangeDto request) {

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new EntityNotFoundException("Счет не найден."));

        if (!account.getIsEnabled()) throw new AccountIsDisabledException("Счет закрыт.");

        double newBalance = account.getAmount() + request.amount();

        if (newBalance < 0) throw new InsufficientFundsException("Недостаточно средств для снятия.");

        account.setAmount(newBalance);

        accountRepository.save(account);
    }

}
