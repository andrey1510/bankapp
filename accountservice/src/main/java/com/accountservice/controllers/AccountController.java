package com.accountservice.controllers;

import com.accountservice.dto.AccountBalanceChangeDto;
import com.accountservice.dto.BalanceUpdateRequestDto;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/cash-update")
    @PreAuthorize("hasAuthority('SCOPE_accountservice.post')")
    public ResponseEntity<?> updateBalanceCash(
        @RequestBody AccountBalanceChangeDto request
    ) {
        log.info("Received updated account balance");
        try {
            accountService.updateBalanceCash(request);
            return ResponseEntity.ok().build();
        } catch (InsufficientFundsException e) {
            return ResponseEntity
                .badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/transfer-update")
    @PreAuthorize("hasAuthority('SCOPE_accountservice.post')")
    public ResponseEntity<?> updateBalanceTransfer(
        @RequestBody BalanceUpdateRequestDto request
    ) {
        log.info("Received updated account balance");
        try {
            accountService.updateBalanceTransfer(request);
            return ResponseEntity.ok().build();
        } catch (InsufficientFundsException e) {
            return ResponseEntity
                .badRequest()
                .body(e.getMessage());
        }
    }

}