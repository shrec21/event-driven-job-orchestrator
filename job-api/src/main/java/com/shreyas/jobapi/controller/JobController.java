package com.shreyas.jobapi.controller;

import com.shreyas.jobapi.db.JobEntity;
import com.shreyas.jobapi.db.JobRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/jobs")
public class JobController {

  private final JobRepo repo;

  public JobController(JobRepo repo) {
    this.repo = repo;
  }

  @PostMapping("/demo")
  public JobEntity createDemoJob() {
    JobEntity job = new JobEntity();
    job.type = "DEMO";
    job.status = "CREATED";
    return repo.save(job);
  }

  @GetMapping
  public List<JobEntity> listJobs() {
    return repo.findAll();
  }
}