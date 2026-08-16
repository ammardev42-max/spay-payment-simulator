package com.ammarbhatkar.SPay.outbox.consumer;

import com.ammarbhatkar.SPay.outbox.service.ProcessedEventService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final String CONSUMER_NAME = "payment-event-consumer";

    private final ProcessedEventService processedEventService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spay.kafka.payment-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePaymentEvent(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            UUID eventId = UUID.fromString(root.get("eventId").asText());
            String eventType = root.get("eventType").asText();

            processedEventService.markProcessed(
                    eventId,
                    eventType,
                    CONSUMER_NAME,
                    message
            );

            log.info("Consumed Kafka payment event {} of type {}", eventId, eventType);
        } catch (Exception exception) {
            log.error("Failed to consume Kafka payment event", exception);
            throw new IllegalStateException("Failed to consume Kafka payment event", exception);
        }
    }
}
