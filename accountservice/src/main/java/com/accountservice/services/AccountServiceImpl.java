package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.entities.Account;
import com.accountservice.exceptions.AccountNotFoundException;
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
    public void updateBalanceCash(AccountBalanceChangeDto request) {

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new EntityNotFoundException("Счет не найден."));

        double newBalance = account.getAmount() + request.amount();

        if (newBalance < 0) throw new InsufficientFundsException("Недостаточно средств для снятия.");

        account.setAmount(newBalance);

        accountRepository.save(account);
    }


    @Override
    public void updateBalanceTransfer(BalanceUpdateRequestDto request) {

        Account senderAccount = accountRepository.findById(request.senderAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден."));
        Account recepientAccount = accountRepository.findById(request.recipientAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден."));

        double newSenderBalance = senderAccount.getAmount() - request.senderAccountBalanceChange();

        if (newSenderBalance < 0) throw new InsufficientFundsException("Недостаточно средств для перевода.");

        double newRecepientBalance = recepientAccount.getAmount() + request.recipientAccountBalanceChange();

        senderAccount.setAmount(newSenderBalance);
        recepientAccount.setAmount(newRecepientBalance);

        accountRepository.save(senderAccount);
        accountRepository.save(recepientAccount);
    }

}
