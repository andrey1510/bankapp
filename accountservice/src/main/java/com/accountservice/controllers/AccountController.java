package com.accountservice.controllers;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/cash")
    public ResponseEntity<?> updateBalance(
        @RequestBody AccountBalanceChangeDto request
    ) {
        try {
            accountService.updateAccountBalance(request);
            return ResponseEntity.ok().build();
        } catch (InsufficientFundsException e) {
            return ResponseEntity
                .badRequest()
                .body(e.getMessage());
        }
    }
}