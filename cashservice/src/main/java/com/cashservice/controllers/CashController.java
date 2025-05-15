package com.cashservice.controllers;

import com.cashservice.dto.CashRequest;
import com.cashservice.services.CashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deposit(@Valid @RequestBody CashRequest request) {
        cashService.processDeposit(request);
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void withdraw(@Valid @RequestBody CashRequest request) {
        cashService.processWithdraw(request);
    }
}
