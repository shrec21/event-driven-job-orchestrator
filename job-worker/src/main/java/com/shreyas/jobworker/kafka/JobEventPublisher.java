package com.shreyas.jobworker.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shreyas.jobworker.dto.JobCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobEventPublisher {

  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper mapper;

  public JobEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper mapper) {
    this.kafka = kafka;
    this.mapper = mapper;
  }

  public void republishJobCreated(JobCreatedEvent event) {
    try {
      String json = mapper.writeValueAsString(event);
      kafka.send("jobs.created", event.id().toString(), json);
    } catch (Exception e) {
      throw new RuntimeException("Failed to republish Kafka event", e);
    }
  }
}