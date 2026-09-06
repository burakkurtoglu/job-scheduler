package com.wornexe.job_scheduler.service;

import com.wornexe.job_scheduler.entity.Job;
import com.wornexe.job_scheduler.util.*;
import com.wornexe.job_scheduler.repository.JobRepository;
import com.wornexe.job_scheduler.util.ParserRules;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SchedulerService {

    public final ThreadPoolExecutor executor;


    private final JobRepository jobRepository;


    public SchedulerService(@Qualifier("jobExec") ThreadPoolExecutor executor, JobRepository jobRepository){
        this.executor = executor;
        this.jobRepository = jobRepository;
    }

    public void work(Job job){
        job.state = Job.State.IN_QUEUE;
        try {
            executor.execute(() -> {
                        job.state = Job.State.PROCESSING;
                        int jobLong = job.getPayload();
                        jobLong /= (job.getRetryCount() + 1);

                        if (jobLong > 50 && job.getRetryCount() > 3) { // totally failed
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.err.println("sleep interrupted");
                            }
                            job.state = Job.State.FAILED;
                            job.setLastExecutionTime(LocalDateTime.now());
                            jobRepository.save(job);
                        } else if (jobLong > 50 && job.getRetryCount() < 3) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.err.println("sleep interrupted");
                            }
                            job.state = Job.State.WAITING_RETRY;
                            job.setRetryCount(job.getRetryCount() + 1);
                            job.setNextExecutionTime(LocalDateTime.now().plusSeconds(3)); // add delay to the next execution time
                            job.setLastExecutionTime(LocalDateTime.now());
                            jobRepository.save(job);
                        } else if (jobLong <= 50 && job.getRetryCount() < 3) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.err.println("sleep interrupted");
                            }
                            job.state = Job.State.FINISHED;
                            job.setLastExecutionTime(LocalDateTime.now());
                            jobRepository.save(job);
                        }
                    }
            );
        } catch (RejectedExecutionException e){
            job.state = Job.State.WAITING_RETRY;
            job.setNextExecutionTime(LocalDateTime.now().plusSeconds(3)); //set delay for retry next

            jobRepository.save(job);
            // throw  new RejectedExecutionException("Queue is Full");
        }
        }

    public Job saveNewJob(String cronStr, int payload){
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> parsedStringMap = cp.cronParser(cronStr);
        NextExecutionTime nextExecutionTime = new NextExecutionTime();
        LocalDateTime nextExecutionTimeLdt = nextExecutionTime.nextExecutionTime(
                parsedStringMap.get("month"),
                parsedStringMap.get("day"),
                parsedStringMap.get("hour"),
                parsedStringMap.get("minute"),
                parsedStringMap.get("weekday"),
                LocalDateTime.now()
                );
        Job job = new Job(Job.State.WAITING_TIME, 0, payload, nextExecutionTimeLdt);
        job = jobRepository.save(job);
        return job;
    }
    }



