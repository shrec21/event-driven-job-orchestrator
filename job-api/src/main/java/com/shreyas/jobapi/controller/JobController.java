package com.shreyas.jobapi.controller;

import com.shreyas.jobapi.db.JobEntity;
import com.shreyas.jobapi.db.JobRepo;
import com.shreyas.jobapi.dto.CreateJobRequest;
import com.shreyas.jobapi.dto.JobDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.shreyas.jobapi.dto.JobCreatedEvent;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import com.shreyas.jobapi.outbox.OutboxService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/v1/jobs")
public class JobController {

  private final JobRepo repo;
    private final OutboxService outbox;

    public JobController(JobRepo repo, OutboxService outbox) {
    this.repo = repo;
    this.outbox = outbox;
    }

    @PostMapping
    @Transactional
    public JobDto createJob(@RequestBody @Valid CreateJobRequest req) {
    JobEntity job = new JobEntity();
    job.type = req.type();
    job.status = req.status();

    JobEntity saved = repo.save(job);

    // event stored in DB, not sent yet
    outbox.enqueue(
        "Job",
        saved.id,
        "JobCreated",
        "jobs.created",
        new com.shreyas.jobapi.dto.JobCreatedEvent(saved.id, saved.type, saved.status, saved.createdAt)
    );

    return toDto(saved);
    }

  @PostMapping("/demo")
  public JobDto createDemoJob() {
    JobEntity job = new JobEntity();
    job.type = "DEMO";
    job.status = "CREATED";
    return toDto(repo.save(job));
  }

  @GetMapping
  public List<JobDto> listJobs() {
    return repo.findAll().stream().map(this::toDto).toList();
  }

  @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJob(@PathVariable UUID id) {
    return repo.findById(id)
        .map(this::toDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

  private JobDto toDto(JobEntity e) {
    return new JobDto(e.id, e.type, e.status, e.createdAt);
  }
}