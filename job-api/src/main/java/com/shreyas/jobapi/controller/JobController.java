package com.shreyas.jobapi.controller;

import com.shreyas.jobapi.db.JobEntity;
import com.shreyas.jobapi.db.JobRepo;
import com.shreyas.jobapi.dto.CreateJobRequest;
import com.shreyas.jobapi.dto.JobDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.shreyas.jobapi.dto.JobCreatedEvent;
import com.shreyas.jobapi.kafka.JobEventPublisher;

import java.util.List;

@RestController
@RequestMapping("/v1/jobs")
public class JobController {

  private final JobRepo repo;
  private final JobEventPublisher publisher;

    public JobController(JobRepo repo, JobEventPublisher publisher) {
    this.repo = repo;
    this.publisher = publisher;
    }

  @PostMapping("/demo")
  public JobDto createDemoJob() {
    JobEntity job = new JobEntity();
    job.type = "DEMO";
    job.status = "CREATED";
    return toDto(repo.save(job));
  }

  @PostMapping
    public JobDto createJob(@RequestBody @Valid CreateJobRequest req) {
    JobEntity job = new JobEntity();
    job.type = req.type();
    job.status = req.status();

    JobEntity saved = repo.save(job);

    publisher.publishJobCreated(
        new JobCreatedEvent(saved.id, saved.type, saved.status, saved.createdAt)
    );

    return toDto(saved);
    }
  @GetMapping
  public List<JobDto> listJobs() {
    return repo.findAll().stream().map(this::toDto).toList();
  }

  private JobDto toDto(JobEntity e) {
    return new JobDto(e.id, e.type, e.status, e.createdAt);
  }
}