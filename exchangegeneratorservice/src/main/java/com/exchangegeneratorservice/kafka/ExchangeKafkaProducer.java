package com.exchangegeneratorservice.kafka;

import com.exchangegeneratorservice.dto.CurrencyRateDto;
import com.exchangegeneratorservice.services.ExchangeGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeKafkaProducer {

    private final ExchangeGenerationService exchangeGenerationService;
    private final KafkaTemplate<String, List<CurrencyRateDto>> kafkaTemplate;

    @Value("${spring.kafka.topic.rates}")
    private String currencyRatesTopic;

    public void sendRates() {
        try {
            kafkaTemplate.send(currencyRatesTopic, "rates_key", exchangeGenerationService.getCurrencyRateDtos());
        } catch (Exception e) {
            log.error("Error sending rates", e);
        }
    }
}



