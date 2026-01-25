package com.shreyas.jobapi.dto;

import java.time.Instant;
import java.util.UUID;

public record JobDto(
  UUID id,
  String type,
  String status,
  Instant createdAt
) {}