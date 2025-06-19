package com.exchangegeneratorservice.kafka;

import com.exchangegeneratorservice.dto.kafka.CurrencyRatesBatchDto;
import com.exchangegeneratorservice.services.ExchangeGenerationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import java.time.Duration;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.ConsumerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
    "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
    "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "spring.kafka.consumer.group-id=test-group",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
    "spring.kafka.consumer.properties.spring.json.use.type.headers=false",
    "spring.kafka.consumer.properties.spring.json.value.default.type=com.exchangegeneratorservice.dto.kafka.CurrencyRatesBatchDto" // Отключаем планировщик
})
@EmbeddedKafka(
    topics = "${spring.kafka.topic.rates}",
    partitions = 1
)
@DirtiesContext
class ExchangeKafkaProducerTest {

    @Autowired
    private ExchangeKafkaProducer producer;

    @Mock
    private ExchangeGenerationService generationService;

    @Autowired
    private ConsumerFactory<String, CurrencyRatesBatchDto> consumerFactory;

    @Test
    void shouldDeliverMessageToKafka() {
        try (Consumer<String, CurrencyRatesBatchDto> consumer = consumerFactory.createConsumer()) {

            consumer.subscribe(List.of("rates"));
            consumer.poll(Duration.ofMillis(100));
            consumer.commitSync();

            producer.sendRates();

            var records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));

            assertTrue(records.count() > 0);
            assertTrue(StreamSupport.stream(records.records("rates").spliterator(), false)
                .anyMatch(record -> "rates_key".equals(record.key())));
        }
    }
}