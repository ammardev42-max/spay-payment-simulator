package com.ammarbhatkar.SPay.outbox.publisher;

import com.ammarbhatkar.SPay.outbox.entity.OutboxEvent;
import com.ammarbhatkar.SPay.outbox.service.OutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spay.kafka.payment-events-topic}")
    private String paymentEventsTopic;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingPaymentEvents() {
        for (OutboxEvent event : outboxService.findPendingEvents()) {
            try {
                kafkaTemplate.send(
                        paymentEventsTopic,
                        event.getAggregateId().toString(),
                        toKafkaMessage(event)
                ).get();

                outboxService.markPublished(event.getId());
                log.info("Published outbox event {} to Kafka topic {}", event.getId(), paymentEventsTopic);
            } catch (Exception exception) {
                outboxService.markFailed(event.getId(), exception.getMessage());
                log.warn("Failed to publish outbox event {} to Kafka", event.getId(), exception);
            }
        }
    }

    private String toKafkaMessage(OutboxEvent event) throws JsonProcessingException {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("eventId", event.getId());
        message.put("eventType", event.getEventType());
        message.put("aggregateType", event.getAggregateType());
        message.put("aggregateId", event.getAggregateId());
        message.put("payload", objectMapper.readTree(event.getPayload()));
        message.put("createdAt", event.getCreatedAt());
        return objectMapper.writeValueAsString(message);
    }
}
