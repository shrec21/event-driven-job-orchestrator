package com.shreyas.jobapi.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxService {

  private final OutboxRepo repo;
  private final ObjectMapper mapper;

  public OutboxService(OutboxRepo repo, ObjectMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  public void enqueue(String aggregateType, UUID aggregateId, String eventType, String topic, Object payloadObj) {
    try {
      OutboxEventEntity e = new OutboxEventEntity();
      e.id = UUID.randomUUID();
      e.aggregateType = aggregateType;
      e.aggregateId = aggregateId;
      e.eventType = eventType;
      e.topic = topic;
      e.payload = mapper.writeValueAsString(payloadObj);
      e.createdAt = Instant.now();
      e.processedAt = null;

      repo.save(e);
    } catch (Exception ex) {
      throw new RuntimeException("Failed to serialize outbox payload", ex);
    }
  }
}