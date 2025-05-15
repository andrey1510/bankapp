package com.transferservice.clients;

import com.transferservice.dto.SuspicionOperation;
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

    public SuspicionOperation checkTransferOperation(TransferRequest request) {
        return restTemplate.postForObject(
            baseUrl + "/cash",
            request,
            SuspicionOperation.class
        );
    }
}
