package com.transferservice.controllers;

import com.transferservice.dto.TransferRequestDto;
import com.transferservice.services.TransferService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    private TransferRequestDto validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new TransferRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            2L,
            "EUR",
            "login1",
            "login2"
        );

    }

    @Test
    void processTransfer_WithValidRequest_ShouldReturnAccepted() {
        doNothing().when(transferService).processTransfer(any(TransferRequestDto.class));

        ResponseEntity<?> response = transferController.processTransfer(validRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(transferService, times(1)).processTransfer(validRequest);
    }

} 