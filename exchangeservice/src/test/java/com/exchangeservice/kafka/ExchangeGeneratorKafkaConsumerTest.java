package com.exchangeservice.kafka;

import com.exchangeservice.dto.kafka.CurrencyRateDto;
import com.exchangeservice.dto.kafka.CurrencyRatesBatchDto;
import com.exchangeservice.services.RateService;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ExchangeGeneratorKafkaConsumerTest {

    @Mock
    private RateService rateService;

    @InjectMocks
    private ExchangeGeneratorKafkaConsumer exchangeGeneratorKafkaConsumer;

    private MockConsumer<String, CurrencyRatesBatchDto> mockConsumer;
    private MockProducer<String, CurrencyRatesBatchDto> mockProducer;

    @BeforeEach
    void setUp() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.LATEST);
        mockProducer = new MockProducer<>(true, new StringSerializer(), new JsonSerializer<>());
    }

    @Test
    void consumeRates_ShouldProcessRatesBatchSuccessfully() {
        CurrencyRatesBatchDto batch = new CurrencyRatesBatchDto(List.of(new CurrencyRateDto(
            "Доллар",
            "USD",
            new BigDecimal("75.50"),
            LocalDateTime.now()
        )));

        exchangeGeneratorKafkaConsumer.consumeRates(batch);

        verify(rateService).saveRates(batch);
        verifyNoMoreInteractions(rateService);
    }

}
