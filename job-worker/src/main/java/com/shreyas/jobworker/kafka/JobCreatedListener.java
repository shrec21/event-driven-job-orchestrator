package com.shreyas.jobworker.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shreyas.jobworker.db.JobEntity;
import com.shreyas.jobworker.db.JobRepo;
import com.shreyas.jobworker.dto.JobCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobCreatedListener {

  private final JobRepo repo;
  private final ObjectMapper mapper;
  private final JobEventPublisher publisher;

  public JobCreatedListener(JobRepo repo, ObjectMapper mapper, JobEventPublisher publisher) {
    this.repo = repo;
    this.mapper = mapper;
    this.publisher = publisher;
  }

  @KafkaListener(topics = "jobs.created")
  public void onJobCreated(String message) throws Exception {
    JobCreatedEvent event = mapper.readValue(message, JobCreatedEvent.class);

    JobEntity job = repo.findById(event.id())
        .orElseThrow(() -> new IllegalStateException("Job not found: " + event.id()));

    // Idempotency guard: if already done, ignore duplicates safely
    if ("SUCCEEDED".equals(job.status) || "FAILED".equals(job.status)) {
      System.out.println("🟦 Ignoring already-finished job " + job.id + " status=" + job.status);
      return;
    }

    try {
      // Mark RUNNING
      job.status = "RUNNING";
      job.lastError = null;
      repo.save(job);
      System.out.println("👷 RUNNING job " + job.id + " attempt=" + job.attempts);

      // Do work based on type (we’ll add “real” handlers later)
      doWork(job);

      // Mark SUCCEEDED
      job.status = "SUCCEEDED";
      repo.save(job);
      System.out.println("✅ SUCCEEDED job " + job.id);

    } catch (Exception e) {
      // Increment attempts + store error
      job.attempts = job.attempts + 1;
      job.lastError = e.getMessage();
      System.out.println("❌ FAILED attempt=" + job.attempts + " for job " + job.id + " reason=" + job.lastError);

      if (job.attempts >= 3) {
        job.status = "FAILED";
        repo.save(job);
        System.out.println("🟥 Marked FAILED job " + job.id + " after " + job.attempts + " attempts");
        return;
      }

      // Set back to CREATED (retryable) and persist
      job.status = "CREATED";
      repo.save(job);

      // Backoff (simple)
      Thread.sleep(1000);

      // Re-queue by republishing the same event
      publisher.republishJobCreated(event);
      System.out.println("🔁 Re-queued job " + job.id + " for retry");
    }
  }

  private void doWork(JobEntity job) throws Exception {
    // Simulate different behaviors by type
    if ("WEBHOOK".equals(job.type)) {
        // Fail first 2 attempts, succeed on 3rd
        if (job.attempts < 2) {
            throw new RuntimeException("Simulated webhook failure (attempt " + (job.attempts + 1) + ")");
        }
        Thread.sleep(1000);
        return;
    }

    if ("REPORT".equals(job.type)) {
      Thread.sleep(3000);
      return;
    }

    // default EMAIL and others
    Thread.sleep(1000);
  }
}