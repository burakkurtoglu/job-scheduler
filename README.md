# Job Scheduler

A single-machine job scheduler built from scratch in Java/Spring Boot, as a learning project focused on understanding core concurrency and scheduling concepts rather than using an off-the-shelf library (e.g. Quartz).

## Overview

The system accepts jobs defined by a cron expression and a payload, computes their next execution time, and runs them on a fixed-size thread pool when their time arrives. Instead of polling the database on a fixed interval, the scheduler uses a **wake-up mechanism**: it always sleeps exactly until the next known execution time, and reschedules itself whenever a newer, earlier job appears.

## Tech Stack

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- JUnit 5

## Core Concepts

### Cron Parsing
A hand-written cron parser (`CronParser`, field-specific parsers for minute/hour/day/month/weekday, plus a `Validator`) turns a 5-field cron string into structured, validated data — no external cron library.

### Next Execution Time Calculation
`NextExecutionTime` walks forward from "now" through month → day → hour → minute to find the next valid run time, handling month rollovers, leap years, and POSIX-style OR logic when both day-of-month and day-of-week are restricted.

### Job Lifecycle
Each `Job` moves through states: `WAITING_TIME` → `IN_QUEUE` → `PROCESSING` → (`FINISHED` | `WAITING_RETRY` | `FAILED`). Retries reuse `nextExecutionTime` to schedule the next attempt, so retry jobs and normal waiting jobs are handled uniformly by the scheduler.

### Thread Pool Execution
Jobs run on a bounded `ThreadPoolExecutor` (5 core / 10 max threads, bounded queue). If the pool rejects a job (queue full), the job is not lost — it's marked `WAITING_RETRY` and rescheduled, without incrementing the retry count (since it never actually ran).

### Wake-Up Mechanism
`WakeUpService` holds a single active `ScheduledFuture`, always pointing at the earliest known job. When a new or updated job has an earlier `nextExecutionTime` than what's currently scheduled, the active schedule is cancelled and replaced. All check-then-act sequences on the active schedule are synchronized to prevent race conditions between:
- the applicaton startup trigger,
- new job creation,
- and the scheduler's own thread re-arming itself after each run.

This avoids constant database polling — the system only wakes up exactly when there's work to do.

## API

### Create a job
```
POST /job
Content-Type: application/json

{
  "cronStr": "0 12 * * *",
  "payload": 42
}
```

**Response**
```json
{
  "id": 1,
  "nextExecutionTime": "2026-09-07T12:00:00",
  "lastExecutionTime": null
}
```

### Get a job by ID
```
GET /job/{id}
```

**Response**
```json
{
  "id": 1,
  "nextExecutionTime": "2026-09-07T12:00:00",
  "lastExecutionTime": null
}
```

### Errors
Invalid cron expressions return `400 Bad Request` with a descriptive message, handled centrally via `@RestControllerAdvice`.

## Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE job_scheduler;
   ```
2. Configure `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/job_scheduler
   spring.datasource.username=postgres
   spring.datasource.password=${DB_PASSWORD}
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Set the `DB_PASSWORD` environment variable (e.g. in your IDE's run configuration).
4. Run the application:
   ```
   mvn spring-boot:run
   ```

Tables are created automatically on startup via `ddl-auto=update`.

## Design Notes / Scoped Decisions

- **Single-machine only.** A distributed version was considered and deliberately deferred to a separate future project.
- **`schedule()` over `scheduleAtFixedRate()`.** The wake-up mechanism needs a dynamic, one-off delay each time, not a fixed interval — `ScheduledExecutorService.schedule(...)` fits that need directly.
- **No cron-library dependency.** Parsing and next-time calculation are implemented manually as the core learning goal of the project, at the cost of not covering every edge case a mature library (e.g. `cron-utils`) would.
- **`synchronized` over `AtomicReference`.** The wake-up mechanism's critical section spans multiple side effects (cancel + reschedule + reference update), which fits a lock-based approach more cleanly than a CAS loop.

## Testing

JUnit 5 tests cover the next-execution-time calculator and the scheduler's core branches (finished, waiting-retry, and thread-pool-rejection paths), including concurrency scenarios using `CountDownLatch`.

## Possible Next Steps

- Reconsider `@Transactional` boundaries as the codebase grows (current sequential `save()` → `wakeUp()` flow relies on each `save()` committing independently before the next read).
- Add pagination/listing endpoints beyond single-job lookup.
- Add structured logging in place of the ad-hoc debug output removed during cleanup.
