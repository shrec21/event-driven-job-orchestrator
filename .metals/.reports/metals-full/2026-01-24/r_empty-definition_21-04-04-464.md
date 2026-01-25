error id: file://<WORKSPACE>/job-api/src/main/java/com/shreyas/jobapi/outbox/OutboxPublisher.java:java/util/List#
file://<WORKSPACE>/job-api/src/main/java/com/shreyas/jobapi/outbox/OutboxPublisher.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 751
uri: file://<WORKSPACE>/job-api/src/main/java/com/shreyas/jobapi/outbox/OutboxPublisher.java
text:
```scala
package com.shreyas.jobapi.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @@List<OutboxEventEntity> batch = repo.findAll().stream()
        .filter(e -> e.processedAt == null)
        .limit(20)
        .toList();

    for (OutboxEventEntity e : batch) {
      kafka.send(e.topic, e.aggregateId.toString(), e.payload);
      e.processedAt = Instant.now();
      repo.save(e);
      System.out.println("📤 Outbox published " + e.eventType + " id=" + e.id);
    }
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#