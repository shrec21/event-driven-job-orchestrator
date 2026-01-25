package com.shreyas.jobworker.dto;

import java.time.Instant;
import java.util.UUID;

public record JobCreatedEvent(
  UUID id,
  String type,
  String status,
  Instant createdAt
) {}