package com.ammarbhatkar.SPay.outbox.service.impl;

import com.ammarbhatkar.SPay.outbox.entity.ProcessedEvent;
import com.ammarbhatkar.SPay.outbox.repository.ProcessedEventRepository;
import com.ammarbhatkar.SPay.outbox.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessedEventServiceImpl implements ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    @Transactional
    public void markProcessed(UUID eventId, String eventType, String consumerName, String payload) {
        if (processedEventRepository.existsByEventIdAndConsumerName(eventId, consumerName)) {
            return;
        }

        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .consumerName(consumerName)
                .payload(payload)
                .processedAt(Instant.now())
                .build();

        processedEventRepository.save(processedEvent);
    }
}
