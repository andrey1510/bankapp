package com.exchangegeneratorservice.clients;

import com.exchangegeneratorservice.dto.CurrencyRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ExchangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeClient exchangeClient;

    @BeforeEach
    void setUp() {
        exchangeClient.exchangeServiceUrl = "http://test.com";
    }

    @Test
    void generateAndSendRates_ShouldSendCorrectRates() {

        exchangeClient.generateAndSendRates();

        verify(restTemplate).postForObject(
            eq("http://test.com"),
            argThat((List<CurrencyRate> rates) ->
                rates.size() == 3 &&
                    rates.stream().anyMatch(r -> r.currency().equals("RUR"))),
            eq(Void.class)
        );
    }

    @Test
    void generateRate_ShouldReturnValueWithinRange() {
        double base = 70.0;
        double spread = 20.0;

        for (int i = 0; i < 100; i++) {
            double rate = exchangeClient.generateRate(base, spread);

            assertTrue(rate >= base && rate <= (base + spread));
        }
    }

}
