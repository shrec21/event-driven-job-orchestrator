package com.shreyas.jobapi.db;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class JobEntity {

  @Id
  @GeneratedValue
  public UUID id;

  public String type;

  public String status;

  @Column(name = "created_at")
  public Instant createdAt = Instant.now();
}