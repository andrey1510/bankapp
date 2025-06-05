package com.transferservice.clients;

import com.transferservice.dto.SuspicionOperationDto;
import com.transferservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BlockerClient {

    private final RestTemplate restTemplate;

    @Value("${blockerservice.url}")
    private String baseUrl;

    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        return restTemplate.postForObject(
            baseUrl + "/transfer",
            request,
            SuspicionOperationDto.class
        );
    }
}
