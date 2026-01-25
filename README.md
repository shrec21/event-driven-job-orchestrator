# Event-Driven Job Orchestration Platform


A production-style, event-driven backend system for asynchronously processing jobs with reliability guarantees.  
Built using **Spring Boot**, **Apache Kafka**, and **PostgreSQL**, this project demonstrates real-world patterns such as retries, idempotency, and the **Transactional Outbox**.


---


## 🚀 Overview


This system allows clients to submit jobs via a REST API. Jobs are processed asynchronously by background workers using Kafka, with durable state stored in PostgreSQL.


The platform is designed to handle failures gracefully and guarantee that jobs are **never lost**, even in the presence of crashes or message broker outages.


---


## 🧠 Architecture



Client
|
| HTTP (create job)
v
job-api (Spring Boot)
| - persists job
| - writes outbox event (same DB transaction)
|
v
PostgreSQL
|
| OutboxPublisher (scheduled)
v
Apache Kafka (jobs.created topic)
|
v
job-worker (Spring Boot)
| - consumes events
| - executes job
| - retries on failure
v
PostgreSQL (job status updates)



---


## 🔁 Job Lifecycle


Jobs follow a durable state machine:



CREATED → RUNNING → SUCCEEDED
→ FAILED (after max retries)



Each transition is persisted to the database to survive restarts and crashes.


---


## ✨ Key Features


### Event-Driven Processing
- Kafka used as an asynchronous work queue
- Decouples job submission from execution


### Retry & Failure Handling
- Configurable retry attempts
- Backoff between retries
- Terminal `FAILED` state after max attempts
- Error details stored for debugging


### Idempotency
- Duplicate Kafka events are safely ignored once a job reaches a terminal state


### Transactional Outbox Pattern
- Job creation and event emission are atomic
- Events are first stored in a database outbox table
- Background publisher reliably delivers events to Kafka
- Prevents message loss during partial failures


### Durable State
- PostgreSQL is the source of truth
- Job progress survives restarts, crashes, and redeployments


---


## 🗄️ Database Schema


### `jobs`
| Column | Purpose |
|------|--------|
| `id` | Job identifier |
| `type` | Job category (EMAIL, WEBHOOK, etc.) |
| `status` | Job state |
| `attempts` | Retry count |
| `last_error` | Failure reason |
| `created_at` | Audit timestamp |


### `outbox_events`
| Column | Purpose |
|------|--------|
| `id` | Event identifier |
| `aggregate_type` | Domain entity (Job) |
| `aggregate_id` | Job ID |
| `event_type` | Event name |
| `topic` | Kafka topic |
| `payload` | JSON event data |
| `processed_at` | Delivery timestamp |


---


## 🛠️ Tech Stack


- Java 17
- Spring Boot
- Spring Data JPA
- Apache Kafka
- PostgreSQL
- Docker & Docker Compose


---


## ▶️ Running the Project


### Prerequisites
- Java 17+
- Docker & Docker Compose


### Start infrastructure
```bash
docker compose up -d
Run services
# Job API
cd job-api
./mvnw spring-boot:run


# Job Worker (separate terminal)
cd job-worker
./mvnw spring-boot:run
```

📬 Example API Usage

Create a job:

curl -X POST http://localhost:8080/v1/jobs \
  -H "Content-Type: application/json" \
  -d '{"type":"WEBHOOK","status":"CREATED"}'

Check job status:

curl http://localhost:8080/v1/jobs/{jobId}
