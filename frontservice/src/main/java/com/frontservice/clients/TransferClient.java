package com.frontservice.clients;

import com.frontservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TransferClient {

    private final RestTemplate restTemplate;

    public void sendTransferRequest(TransferRequestDto transferRequest) {
        restTemplate.postForEntity(
            "http://localhost:8891/api/transfers/transfer",
            transferRequest,
            Void.class
        );
    }

}
