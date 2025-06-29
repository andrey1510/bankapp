package com.accountservice.services;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.dto.kafka.NotificationRequestDto;
import com.accountservice.entities.Account;
import com.accountservice.exceptions.AccountNotFoundException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.kafka.NotificationProducer;
import com.accountservice.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
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
        log.info("Account saved: {}", account);

        notificationProducer.sendNotifications(
            new NotificationRequestDto(account.getUser().getEmail(), createCashMessage(request, account.getCurrency())),
            request.login()
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
        log.info("Account saved: {}", senderAccount);
        accountRepository.save(recipientAccount);
        log.info("Account saved: {}", recipientAccount);

        notificationProducer.sendNotifications(
            new NotificationRequestDto(
                senderAccount.getUser().getEmail(), createTransferMessage(request, senderAccount.getCurrency())),
            request.senderLogin()
        );
    }

    private String createCashMessage(AccountBalanceChangeDto request, String currency) {

        String operationType = "пополнению";
        BigDecimal amount = request.amount();

        if(request.amount().compareTo(BigDecimal.ZERO) < 0) {
           amount = amount.negate().setScale(2, RoundingMode.HALF_UP);
            operationType = "снятию со";
        }

        return String.format("%s была проведена операция по %s счета %s пользователя %s  на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            operationType,
            request.accountId(),
            request.login(),
            amount.setScale(2, RoundingMode.HALF_UP).doubleValue(),
            currency
        );

    }

    private String createTransferMessage(BalanceUpdateRequestDto request, String currency) {
        return String.format("%s была проведена операция по переводу %.2f %s со счета %s пользователя %s на счет %s пользователя %s ",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            request.senderAccountBalanceChange().setScale(2, RoundingMode.HALF_UP).doubleValue(),
            request.senderAccountId(),
            request.senderLogin(),
            request.recipientAccountId(),
            request.recipientLogin(),
            currency
        );
    }


}
