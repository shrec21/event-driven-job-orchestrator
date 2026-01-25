package com.shreyas.jobapi.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

  private final OutboxRepo repo;
  private final KafkaTemplate<String, String> kafka;

  public OutboxPublisher(OutboxRepo repo, KafkaTemplate<String, String> kafka) {
    this.repo = repo;
    this.kafka = kafka;
  }

  // every 1 second
  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void publishUnprocessed() {
    // simple approach: load a few rows, publish, mark processed
    List<OutboxEventEntity> batch = repo.findUnprocessed(PageRequest.of(0, 20));

    for (OutboxEventEntity e : batch) {
      kafka.send(e.topic, e.aggregateId.toString(), e.payload);
      e.processedAt = Instant.now();
      repo.save(e);
      System.out.println("📤 Outbox published " + e.eventType + " id=" + e.id);
    }
  }
}