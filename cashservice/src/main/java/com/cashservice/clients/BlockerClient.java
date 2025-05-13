package com.cashservice.clients;

import com.cashservice.dto.CashIncomingRequest;
import com.cashservice.dto.SuspicionOperationResponse;
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

    public SuspicionOperationResponse checkCashOperation(CashIncomingRequest request) {
        return restTemplate.postForObject(
            baseUrl + "/cash",
            request,
            SuspicionOperationResponse.class
        );
    }
}
