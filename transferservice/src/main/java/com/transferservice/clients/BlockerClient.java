package com.transferservice.clients;

import com.transferservice.dto.SuspicionOperationResponse;
import com.transferservice.dto.TransferRequest;
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

    public SuspicionOperationResponse checkTransferOperation(TransferRequest request) {
        return restTemplate.postForObject(
            baseUrl + "/cash",
            request,
            SuspicionOperationResponse.class
        );
    }
}
