package com.shreyas.jobapi.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

  @Id
  public UUID id;

  public String aggregateType;
  public UUID aggregateId;

  public String eventType;
  public String topic;

  // "text" in Postgres is fine as String here
  public String payload;

  public Instant createdAt;
  public Instant processedAt;
}