package com.transferservice.clients;

import com.transferservice.dto.ConversionRateDto;
import com.transferservice.dto.ConversionRateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExchangeClient {

    private final RestTemplate restTemplate;

    @Value("${exchangeservice.url}")
    private String exchangeServiceUrl;

    public ConversionRateDto getConversionRate(ConversionRateRequestDto requestDto) {
        return restTemplate.postForObject(
            exchangeServiceUrl + "/conversion",
            requestDto,
            ConversionRateDto.class
        );

    }
}
