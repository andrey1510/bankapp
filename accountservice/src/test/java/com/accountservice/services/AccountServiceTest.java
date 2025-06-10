package com.accountservice.services;

import com.accountservice.clients.NotificationClient;
import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.exceptions.AccountNotFoundException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private Account account;
    private User user;
    private AccountBalanceChangeDto cashRequest;
    private BalanceUpdateRequestDto transferRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setCurrency("USD");
        account.setAmount(new BigDecimal("1000.00"));

        cashRequest = new AccountBalanceChangeDto(
            1L,
            new BigDecimal("100.00")
        );

        transferRequest = new BalanceUpdateRequestDto(
            1L,
            new BigDecimal("100.00"),
            2L,
            new BigDecimal("100.00")
        );
    }

    @Test
    void updateBalanceCash_WithValidRequest_ShouldUpdateBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(notificationClient).sendCashNotification(any(), any(), any());

        accountService.updateBalanceCash(cashRequest);

        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertEquals(new BigDecimal("1100.00"), savedAccount.getAmount());
        verify(notificationClient).sendCashNotification(
            cashRequest.amount(),
            account.getCurrency(),
            account.getUser().getEmail()
        );
    }

    @Test
    void updateBalanceCash_WithInsufficientFunds_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        cashRequest = new AccountBalanceChangeDto(1L, new BigDecimal("-1100.00"));

        assertThrows(InsufficientFundsException.class, () -> 
            accountService.updateBalanceCash(cashRequest)
        );
    }

    @Test
    void updateBalanceCash_WithNonExistentAccount_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            accountService.updateBalanceCash(cashRequest)
        );
    }

    @Test
    void updateBalanceTransfer_WithValidRequest_ShouldUpdateBalances() {
        Account recipientAccount = new Account();
        recipientAccount.setId(2L);
        recipientAccount.setUser(user);
        recipientAccount.setCurrency("EUR");
        recipientAccount.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(recipientAccount));
        doNothing().when(notificationClient).sendTransferNotification(any(), any(), any());

        accountService.updateBalanceTransfer(transferRequest);

        verify(accountRepository, times(2)).save(accountCaptor.capture());
        Account savedSenderAccount = accountCaptor.getAllValues().get(0);
        Account savedRecipientAccount = accountCaptor.getAllValues().get(1);

        assertEquals(new BigDecimal("900.00"), savedSenderAccount.getAmount());
        assertEquals(new BigDecimal("600.00"), savedRecipientAccount.getAmount());

        verify(notificationClient).sendTransferNotification(
            transferRequest.senderAccountBalanceChange(),
            account.getCurrency(),
            account.getUser().getEmail()
        );
    }

    @Test
    void updateBalanceTransfer_WithNonExistentAccount_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            accountService.updateBalanceTransfer(transferRequest)
        );
    }
} 