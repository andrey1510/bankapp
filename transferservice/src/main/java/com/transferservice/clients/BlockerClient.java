package com.transferservice.clients;

import com.transferservice.dto.SuspicionOperationDto;
import com.transferservice.dto.TransferRequestDto;
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
public class BlockerClient {

    @Value("${blockerservice.url}")
    protected String blockerUrl;

    @Qualifier("blockerRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        SuspicionOperationDto suspicionOperationDto = restTemplate.postForObject(
            String.format("%s/fraud-check/transfer", blockerUrl),
            request,
            SuspicionOperationDto.class
        );
        log.info("Suspicion operation sent: {}", suspicionOperationDto);
        return suspicionOperationDto;
    }
}
