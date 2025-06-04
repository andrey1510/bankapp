package com.cashservice.controllers;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.exceptions.CashOperationException;
import com.cashservice.services.CashService;
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
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/operation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> processOperation(@Valid @RequestBody CashRequestDto request) {

        log.info("Received request: {}", request);

        try {
            cashService.processOperation(request);
            return ResponseEntity.accepted().build();
        } catch (CashOperationException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        }
    }

}
