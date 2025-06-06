package com.accountservice.services;

import com.accountservice.clients.NotificationClient;
import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.entities.Account;
import com.accountservice.exceptions.AccountNotFoundException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final NotificationClient notificationClient;

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void updateBalanceCash(AccountBalanceChangeDto request) {

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new EntityNotFoundException("Счет не найден."));

        double newBalance = account.getAmount() + request.amount();

        if (newBalance < 0) throw new InsufficientFundsException("Недостаточно средств для снятия.");

        account.setAmount(newBalance);

        accountRepository.save(account);

        notificationClient.sendCashNotification(
            request.amount(),
            account.getCurrency(),
            account.getUser().getEmail()
        );
    }

    @Override
    @Transactional
    public void updateBalanceTransfer(BalanceUpdateRequestDto request) {

        Account senderAccount = accountRepository.findById(request.senderAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден."));
        Account recipientAccount = accountRepository.findById(request.recipientAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден."));

        double newSenderBalance = senderAccount.getAmount() - request.senderAccountBalanceChange();

        if (newSenderBalance < 0) throw new InsufficientFundsException("Недостаточно средств для перевода.");

        double newRecipientBalance = recipientAccount.getAmount() + request.recipientAccountBalanceChange();

        senderAccount.setAmount(newSenderBalance);
        recipientAccount.setAmount(newRecipientBalance);

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        notificationClient.sendTransferNotification(
            request.senderAccountBalanceChange(),
            senderAccount.getCurrency(),
            senderAccount.getUser().getEmail()
        );

    }

}
