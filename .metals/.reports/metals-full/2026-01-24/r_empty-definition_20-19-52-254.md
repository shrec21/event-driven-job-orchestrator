error id: file://<WORKSPACE>/job-worker/src/main/java/com/shreyas/jobworker/kafka/JobCreatedListener.java:_empty_/JobEntity#id#
file://<WORKSPACE>/job-worker/src/main/java/com/shreyas/jobworker/kafka/JobCreatedListener.java
empty definition using pc, found symbol in pc: _empty_/JobEntity#id#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1026
uri: file://<WORKSPACE>/job-worker/src/main/java/com/shreyas/jobworker/kafka/JobCreatedListener.java
text:
```scala
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

  public JobCreatedListener(JobRepo repo, ObjectMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  @KafkaListener(topics = "jobs.created")
  public void onJobCreated(String message) throws Exception {
    JobCreatedEvent event = mapper.readValue(message, JobCreatedEvent.class);

    JobEntity job = repo.findById(event.id())
        .orElseThrow(() -> new IllegalStateException("Job not found: " + event.id()));

    // Update status to RUNNING
    job.status = "RUNNING";
    repo.save(job);
    System.out.println("👷 Worker set RUNNING for job " + job.id@@);

    Thread.sleep(2000);

    
    job.status = "SUCCEEDED";
    repo.save(job);
    System.out.println("✅ Worker set SUCCEEDED for job " + job.id);
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/JobEntity#id#