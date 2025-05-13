package com.transferservice.controllers;

import com.transferservice.dto.TransferRequest;
import com.transferservice.services.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleTransfer(@Valid @RequestBody TransferRequest request) {
        transferService.processTransfer(request);
    }
}
