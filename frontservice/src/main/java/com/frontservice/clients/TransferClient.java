package com.frontservice.clients;

import com.frontservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferClient {

    @Value("${transferservice.url}")
    protected String transferserviceUrl;

    @Qualifier("transferRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendTransferRequest(TransferRequestDto transferRequest) {
        restTemplate.postForEntity(
            String.format("%s/transfers/transfer", transferserviceUrl),
            transferRequest,
            Void.class
        );
        log.info("Transfer request sent");
    }

}
