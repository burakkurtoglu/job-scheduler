package com.wornexe.job_scheduler.config;

import com.wornexe.job_scheduler.entity.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Configuration
public class Worker {
    @Bean(destroyMethod = "shutdown", name = "jobExec")
    public ThreadPoolExecutor jobThreadExecutor(){
        return new ThreadPoolExecutor(
                5,
                10,
                90L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
    @Bean(destroyMethod = "shutdown", name = "execService")
    public ScheduledExecutorService executorService(){
        return Executors.newScheduledThreadPool(2);
    }

}
