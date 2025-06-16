package com.transferservice.clients;
import com.transferservice.dto.ConversionRateDto;
import com.transferservice.dto.ConversionRateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeClient exchangeClient;

    private ConversionRateRequestDto conversionRequest;
    private ConversionRateDto conversionResponse;

    @BeforeEach
    void setUp() {
        exchangeClient.exchangeServiceUrl = "http://exchange-service";
        conversionRequest = new ConversionRateRequestDto("USD", "EUR");
        conversionResponse = new ConversionRateDto(new BigDecimal("0.85"));
    }

    @Test
    void getConversionRate_ShouldCallCorrectEndpoint() {
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(conversionResponse);

        exchangeClient.getConversionRate(conversionRequest);

        verify(restTemplate).postForObject(
            "http://exchange-service/rates/conversion",
            conversionRequest,
            ConversionRateDto.class
        );
    }

    @Test
    void getConversionRate_ShouldReturnResponse() {
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(conversionResponse);

        ConversionRateDto result = exchangeClient.getConversionRate(conversionRequest);

        assertEquals(new BigDecimal("0.85"), result.rate());
    }
}