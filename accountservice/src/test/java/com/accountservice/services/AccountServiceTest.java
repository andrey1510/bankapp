package com.accountservice.services;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

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
            new BigDecimal("100.00"),
            "login"
        );

        transferRequest = new BalanceUpdateRequestDto(
            1L,
            new BigDecimal("100.00"),
            2L,
            new BigDecimal("100.00"),
            "login",
            "login"
        );
    }

    @Test
    void updateBalanceCash_WithInsufficientFunds_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        cashRequest = new AccountBalanceChangeDto(1L, new BigDecimal("-1100.00"),"login");

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
    void updateBalanceTransfer_WithNonExistentAccount_ShouldThrowException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            accountService.updateBalanceTransfer(transferRequest)
        );
    }
} 