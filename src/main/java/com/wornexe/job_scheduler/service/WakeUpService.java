package com.wornexe.job_scheduler.service;

import com.wornexe.job_scheduler.entity.Job;
import com.wornexe.job_scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class WakeUpService {

    private final JobRepository jobRepository;
    private SchedulerService schedulerService;
    public final ScheduledExecutorService scheduledExecutorService;
    public WakeUpService(JobRepository jobRepository, SchedulerService schedulerService,@Qualifier("execService") ScheduledExecutorService scheduledExecutorService){
        this.jobRepository = jobRepository;
        this.schedulerService = schedulerService;
        this.scheduledExecutorService = scheduledExecutorService;
    }



    static class ActiveSchedule{
        Job curNextJob;
        ScheduledFuture<?> scheduledFuture;
        public ActiveSchedule(Job curNextJob, ScheduledFuture<?> scheduledFuture){
            this.curNextJob = curNextJob;
            this.scheduledFuture = scheduledFuture;
        }
    }

    private void updateCurSchedule(Job nextJob){
        long delay = Duration.between(LocalDateTime.now(), nextJob.getNextExecutionTime()).toSeconds();
        curSchedule = new ActiveSchedule(nextJob, scheduledExecutorService.schedule(() -> {
            schedulerService.work(nextJob);
            wakeUp();
        }, delay, TimeUnit.SECONDS));
    }

    private ActiveSchedule curSchedule;
    public void wakeUp(){
        Optional<Job> nextJobOpt = jobRepository.getNextJob();
        if(nextJobOpt.isEmpty()){return;}
        Job nextJob = nextJobOpt.get();
        synchronized (this) {
            if (curSchedule == null) {
                updateCurSchedule(nextJob);

            } else {
                if (curSchedule.curNextJob.getNextExecutionTime().isAfter(nextJob.getNextExecutionTime())) {

                    curSchedule.scheduledFuture.cancel(true);
                    updateCurSchedule(nextJob);

                }
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart(){
        wakeUp();
    }

}
