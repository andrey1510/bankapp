package com.frontservice.clients;

import com.frontservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Service
@RequiredArgsConstructor
public class TransferClient {

    @Value("${transferservice.url.transfer}")
    protected String transferserviceUrl;

    @Qualifier("transferRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendTransferRequest(TransferRequestDto transferRequest) {
        restTemplate.postForEntity(
            transferserviceUrl,
            transferRequest,
            Void.class
        );
    }

}
