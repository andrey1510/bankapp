package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.dto.NotificationRequestDto;
import com.accountservice.entities.Account;
import com.accountservice.exceptions.AccountNotFoundException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.kafka.NotificationProducer;
import com.accountservice.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final NotificationProducer notificationProducer;

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

        notificationProducer.sendNotifications(new NotificationRequestDto(
            account.getUser().getEmail(),
            createCashMessage(request.amount(), account.getCurrency())
        ));
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

        notificationProducer.sendNotifications(new NotificationRequestDto(
            senderAccount.getUser().getEmail(),
            createTransferMessage(request.senderAccountBalanceChange(), senderAccount.getCurrency())
        ));

    }


    private String createCashMessage(BigDecimal amount, String currency) {

        String operationType = "пополнению";

        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            amount = amount.negate().setScale(2, RoundingMode.HALF_UP);
            operationType = "снятию со";
        }

        return String.format("%s была проведена операция по %s счета на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            operationType,
            amount.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            currency);

    }

    private String createTransferMessage(BigDecimal amount, String currency) {
        return String.format("%s была проведена операция по переводу %.2f %s ",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            amount.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            currency);
    }


}
