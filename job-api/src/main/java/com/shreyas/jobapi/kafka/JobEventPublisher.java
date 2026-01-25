package com.shreyas.jobapi.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shreyas.jobapi.dto.JobCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobEventPublisher {

  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper mapper;

  public JobEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper mapper) {
    this.kafka = kafka;
    this.mapper = mapper; // <-- Spring-injected ObjectMapper (has JavaTimeModule)
  }

  public void publishJobCreated(JobCreatedEvent event) {
    try {
      String json = mapper.writeValueAsString(event);
      kafka.send("jobs.created", event.id().toString(), json);
    } catch (Exception e) {
      throw new RuntimeException("Failed to publish Kafka event", e);
    }
  }
}