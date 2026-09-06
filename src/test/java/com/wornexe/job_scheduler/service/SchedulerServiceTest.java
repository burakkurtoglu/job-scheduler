package com.wornexe.job_scheduler.service;

import com.wornexe.job_scheduler.entity.Job;
import com.wornexe.job_scheduler.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private SchedulerService schedulerService;
    @Test
    public void testWorkFinishedPath() throws InterruptedException {
        Job job = new Job(Job.State.WAITING_TIME, 0, 30, LocalDateTime.now());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        schedulerService = new SchedulerService(executor, jobRepository);
        schedulerService.work(job);
        executor.shutdown();
        if(executor.awaitTermination(15, TimeUnit.SECONDS)){
            assertEquals(Job.State.FINISHED, job.state, "unexpected mismatch of state");
            verify(jobRepository, times(1)).save(job);
        }else{
            System.out.println("Execution Terminated");
        }

    }
    @Test
    public void testWorkWaitingRetry() throws InterruptedException {
        LocalDateTime testLdt = LocalDateTime.now();
        Job job = new Job(Job.State.WAITING_TIME, 0, 70, testLdt);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        schedulerService = new SchedulerService(executor, jobRepository);
        schedulerService.work(job);
        executor.shutdown();
        if(executor.awaitTermination(15, TimeUnit.SECONDS)){
            assertEquals(Job.State.WAITING_RETRY, job.state, "unexpected mismatch of state");
            assertEquals(1, job.getRetryCount(), String.format("unexpected mismatch of retry count, expected %d, got %d",1 , job.getRetryCount()));
            assertTrue(testLdt.isBefore(job.getNextExecutionTime()));
            verify(jobRepository, times(1)).save(job);
        }else{
            System.out.println("Execution Terminated");
        }

    }
    @Test
    public void testWorkFAILED() throws InterruptedException {
        LocalDateTime testLdt = LocalDateTime.now();
        Job job = new Job(Job.State.WAITING_TIME, 4, 5000, testLdt);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        schedulerService = new SchedulerService(executor, jobRepository);
        schedulerService.work(job);
        executor.shutdown();
        if(executor.awaitTermination(15, TimeUnit.SECONDS)){
            assertEquals(Job.State.FAILED, job.state, "unexpected mismatch of state");
            assertEquals(4, job.getRetryCount(), String.format("unexpected mismatch of retry count, expected %d, got %d",1 , job.getRetryCount()));
            verify(jobRepository, times(1)).save(job);
        }else{
            System.out.println("Execution Terminated");
        }

    }

    @Test
    public void testWorkAbortPolicy() throws InterruptedException {
        LocalDateTime testLdt = LocalDateTime.now();
        Job job = new Job(Job.State.WAITING_TIME, 0, 30, LocalDateTime.now());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        schedulerService = new SchedulerService(executor, jobRepository);
        CountDownLatch blockLatch = new CountDownLatch(1);
        executor.submit(() -> { // block thread
            try {
                blockLatch.await();
            } catch (InterruptedException ignored) {}
        });

        executor.submit(() -> { // block queue
            try {
                blockLatch.await();
            } catch (InterruptedException ignored) {}
        });

        schedulerService.work(job);

        assertEquals(Job.State.WAITING_RETRY, job.state, "unexpected mismatch");
        assertTrue(testLdt.isBefore(job.getNextExecutionTime()));
        verify(jobRepository, times(1)).save(job);
        blockLatch.countDown();;
        executor.shutdown();
    }
}
