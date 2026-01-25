package com.shreyas.jobworker.db;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class JobEntity {

  @Id
  public UUID id;

  public String type;

  public String status;

  public int attempts;

  public String lastError;

  @Column(name = "created_at")
  public Instant createdAt;
}