package com.blockerservice.controller;

import com.blockerservice.dto.CashRequest;
import com.blockerservice.dto.SuspicionOperationResponse;
import com.blockerservice.dto.TransferRequest;
import com.blockerservice.service.BlockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/fraud-check")
@RequiredArgsConstructor
public class BlockerController {

    private final BlockerService blockerService;

    @PostMapping("/transfer")
    public SuspicionOperationResponse checkTransferOperation(@RequestBody TransferRequest request) {
        return blockerService.checkTransferOperation(request);
    }

    @PostMapping("/cash")
    public SuspicionOperationResponse checkCashOperation(@RequestBody CashRequest request) {
        return blockerService.checkCashOperation(request);
    }

}
