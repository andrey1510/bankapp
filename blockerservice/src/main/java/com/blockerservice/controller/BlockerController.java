package com.blockerservice.controller;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import com.blockerservice.service.BlockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('SCOPE_blockerservice.post')")
    public SuspicionOperationDto checkTransferOperation(@RequestBody TransferRequestDto request) {
        return blockerService.checkTransferOperation(request);
    }

    @PostMapping("/cash")
    @PreAuthorize("hasAuthority('SCOPE_blockerservice.post')")
    public SuspicionOperationDto checkCashOperation(@RequestBody CashRequestDto request) {
        return blockerService.checkCashOperation(request);
    }

}
