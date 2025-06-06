package com.transferservice.controllers;

import com.transferservice.dto.TransferRequestDto;
import com.transferservice.exceptions.SameAccountTransferException;
import com.transferservice.exceptions.TransferOperationException;
import com.transferservice.services.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> processTransfer(@Valid @RequestBody TransferRequestDto request) {

        try {
            transferService.processTransfer(request);
            return ResponseEntity.accepted().build();
        } catch (TransferOperationException | SameAccountTransferException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

}
