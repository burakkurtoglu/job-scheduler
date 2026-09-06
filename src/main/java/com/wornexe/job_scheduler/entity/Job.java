package com.wornexe.job_scheduler.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "JOBS")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-gen")
    @SequenceGenerator(name = "sequence-gen", sequenceName = "sequence-gen-psql")
    private long id;

    public long getId() {
        return id;
    }

    private int payload;

    public int getPayload() {
        return payload;
    }


    private LocalDateTime createdAt;
    @PrePersist
    private void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
    private LocalDateTime nextExecutionTime;

    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    private LocalDateTime lastExecutionTime;

    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    private String cronExpression;
    private int retryCount;

    public int getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(int retry){
        retryCount = retry;
    }

    public enum State{
        IN_QUEUE, PROCESSING, WAITING_TIME, WAITING_RETRY, FAILED, FINISHED
    }
    @Enumerated(EnumType.STRING)
    public State state;

    public Job(){}
    public Job(State state, int retryCount, int payload, LocalDateTime nextExecutionTime){
        this.state = state;
        this.retryCount = retryCount;
        this.payload = payload;
        this.nextExecutionTime = nextExecutionTime;
    }

}
