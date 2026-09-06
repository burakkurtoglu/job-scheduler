package com.wornexe.job_scheduler.repository;

import com.wornexe.job_scheduler.entity.Job;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long>{
    @Query("SELECT j FROM Job j WHERE j.state IN ('WAITING_TIME', 'WAITING_RETRY') ORDER BY j.nextExecutionTime ASC LIMIT 1")
    Optional<Job> getNextJob();
}
