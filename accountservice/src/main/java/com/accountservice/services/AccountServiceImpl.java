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

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        BigDecimal newBalance = account.getAmount().add(request.amount()).setScale(2, RoundingMode.HALF_UP);;

        if (newBalance.compareTo(BigDecimal.ZERO) < 0 )
            throw new InsufficientFundsException("Недостаточно средств для снятия.");

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

        BigDecimal newSenderBalance = senderAccount.getAmount().subtract(request.senderAccountBalanceChange())
            .setScale(2, RoundingMode.HALF_UP);

        if (newSenderBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new InsufficientFundsException("Недостаточно средств для перевода.");

        BigDecimal newRecipientBalance = recipientAccount.getAmount().add(request.recipientAccountBalanceChange())
            .setScale(2, RoundingMode.HALF_UP);

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
