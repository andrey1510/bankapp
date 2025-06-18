package com.exchangeservice.kafka;

import com.exchangeservice.dto.CurrencyRateDto;
import com.exchangeservice.services.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeGeneratorKafkaConsumer {

    private final RateService rateService;

    @KafkaListener(topics = "${spring.kafka.topic.rates}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRates(List<CurrencyRateDto> rates) {
        log.info("Message received: {}", rates);
        rateService.saveRates(rates);
    }
}
