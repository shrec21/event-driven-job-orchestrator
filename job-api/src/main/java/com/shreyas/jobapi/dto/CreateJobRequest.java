package com.shreyas.jobapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateJobRequest(
  @NotBlank String type,
  @NotBlank String status
) {}