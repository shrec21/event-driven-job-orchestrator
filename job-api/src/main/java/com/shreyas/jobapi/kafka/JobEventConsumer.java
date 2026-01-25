package com.shreyas.jobapi.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobEventConsumer {

  @KafkaListener(topics = "jobs.created")
  public void onJobCreated(String message) {
    System.out.println("🔥 KAFKA jobs.created event received: " + message);
  }
}