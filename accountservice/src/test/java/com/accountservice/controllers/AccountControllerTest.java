package com.accountservice.controllers;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountBalanceChangeDto cashRequest;
    private BalanceUpdateRequestDto transferRequest;

    @BeforeEach
    void setUp() {
        cashRequest = new AccountBalanceChangeDto(
            1L,
            BigDecimal.valueOf(100),
            "login"
        );

        transferRequest = new BalanceUpdateRequestDto(
            1L,
            BigDecimal.valueOf(100),
            2L,
            BigDecimal.valueOf(50),
            "login",
            "login"
        );
    }

    @Test
    void updateBalanceCash_WithValidData_ShouldReturnOk() {
        ResponseEntity<?> response = accountController.updateBalanceCash(cashRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(accountService).updateBalanceCash(cashRequest);
    }

    @Test
    void updateBalanceCash_WithInsufficientFunds_ShouldReturnBadRequest() {
        doThrow(new InsufficientFundsException("Недостаточно средств"))
            .when(accountService).updateBalanceCash(any());

        ResponseEntity<?> response = accountController.updateBalanceCash(cashRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Недостаточно средств", response.getBody());
    }

    @Test
    void updateBalanceTransfer_WithValidData_ShouldReturnOk() {
        ResponseEntity<?> response = accountController.updateBalanceTransfer(transferRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(accountService).updateBalanceTransfer(transferRequest);
    }

    @Test
    void updateBalanceTransfer_WithInsufficientFunds_ShouldReturnBadRequest() {
        doThrow(new InsufficientFundsException("Недостаточно средств"))
            .when(accountService).updateBalanceTransfer(any());

        ResponseEntity<?> response = accountController.updateBalanceTransfer(transferRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Недостаточно средств", response.getBody());
    }
} 