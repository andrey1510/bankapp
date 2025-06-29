package com.blockerservice.controller;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import com.blockerservice.service.BlockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/fraud-check")
@RequiredArgsConstructor
public class BlockerController {

    private final BlockerService blockerService;

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('SCOPE_blockerservice.post')")
    public SuspicionOperationDto checkTransferOperation(@RequestBody TransferRequestDto request) {
        log.info("Checking transfer operation");
        return blockerService.checkTransferOperation(request);
    }

    @PostMapping("/cash")
    @PreAuthorize("hasAuthority('SCOPE_blockerservice.post')")
    public SuspicionOperationDto checkCashOperation(@RequestBody CashRequestDto request) {
        log.info("Checking cash operation");
        return blockerService.checkCashOperation(request);
    }

}
