package com.wornexe.job_scheduler.controller;


import com.wornexe.job_scheduler.dto.JobRequestDto;
import com.wornexe.job_scheduler.dto.JobResponseDto;
import com.wornexe.job_scheduler.entity.Job;
import com.wornexe.job_scheduler.repository.JobRepository;
import com.wornexe.job_scheduler.service.SchedulerService;
import com.wornexe.job_scheduler.service.WakeUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class Controller {

    private SchedulerService schedulerService;
    private JobRepository jobRepository;
    private WakeUpService wakeUpService;
    public Controller(SchedulerService schedulerService, JobRepository jobRepository, WakeUpService wakeUpService){
        this.schedulerService = schedulerService;
        this.jobRepository = jobRepository;
        this.wakeUpService = wakeUpService;
    }

    @PostMapping("/job")
    public ResponseEntity<JobResponseDto> createAndSaveJob(@RequestBody JobRequestDto jobRequestDto){
        Job job = schedulerService.saveNewJob(jobRequestDto.cronStr(), jobRequestDto.payload());
        wakeUpService.wakeUp();
        JobResponseDto resp = new JobResponseDto(job.getId(), job.getNextExecutionTime(), job.getLastExecutionTime());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable long id){
        Optional<Job> jobOpt = jobRepository.findById(id);
        if(jobOpt.isEmpty()){
            return ResponseEntity.notFound().build();

        }
        Job job = jobOpt.get();
        JobResponseDto resp = new JobResponseDto(job.getId(), job.getNextExecutionTime(), job.getLastExecutionTime());
        return ResponseEntity.ok(resp);
    }

}
