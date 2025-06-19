package com.exchangegeneratorservice.services;

import com.exchangegeneratorservice.kafka.ExchangeKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ExchangeKafkaProducer exchangeKafkaProducer;


    @Scheduled(fixedRate = 1000)
    public void scheduledGenerateAndSendRates() {
        exchangeKafkaProducer.sendRates();
    }

}
