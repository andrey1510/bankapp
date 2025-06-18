package com.exchangegeneratorservice.kafka;

import com.exchangegeneratorservice.ExchangegeneratorserviceApplication;
import com.exchangegeneratorservice.dto.CurrencyRateDto;
import com.exchangegeneratorservice.services.ExchangeGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ExchangegeneratorserviceApplication.class,
    properties = {
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.bootstrap-servers=localhost:9992",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
    })
@EmbeddedKafka(
    topics = {"rates"},
    partitions = 1,
    brokerProperties = { "listeners=PLAINTEXT://:9992", "port=9992" }
)
public class ExchangeKafkaProducerTest {

    @Autowired
    private ExchangeKafkaProducer exchangeKafkaProducer;

    @Autowired
    private ExchangeGenerationService exchangeGenerationService;

    @Autowired
    private ConsumerFactory<String, List<CurrencyRateDto>> consumerFactory;

    @Test
    void testSendMessage() throws InterruptedException {
        String topic = "rates";
        String groupId = "group";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<CurrencyRateDto>> received = new AtomicReference<>();

        ContainerProperties containerProps = new ContainerProperties(topic);
        KafkaMessageListenerContainer<String, List<CurrencyRateDto>> container =
            new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.setupMessageListener((MessageListener<String, List<CurrencyRateDto>>) record -> {
            received.set(record.value());
            latch.countDown();
        });
        container.getContainerProperties().setGroupId(groupId);
        container.start();

        try {
            exchangeKafkaProducer.sendRates();
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            assertNotNull(received.get());
            assertFalse(received.get().isEmpty());
        } finally {
            container.stop();
        }
    }
}