package com.ammarbhatkar.SPay.outbox.service;

import java.util.UUID;

public interface ProcessedEventService {

    void markProcessed(UUID eventId, String eventType, String consumerName, String payload);
}
