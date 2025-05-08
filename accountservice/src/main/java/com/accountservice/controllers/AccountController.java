package com.accountservice.controllers;

import com.accountservice.dto.AccountDTO;
import com.accountservice.entities.Account;
import com.accountservice.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createBankAccount(
        @PathVariable Long userId,
        @RequestBody AccountDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(accountService.createAccount(userId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
        @PathVariable UUID id,
        @PathVariable Long userId,
        @RequestBody AccountDTO dto
    ) {
        return ResponseEntity.ok(accountService.updateAccount(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(
        @PathVariable UUID id,
        @PathVariable Long userId
    ) {
        accountService.deleteAccount(id, userId);
        return ResponseEntity.noContent().build();
    }
}