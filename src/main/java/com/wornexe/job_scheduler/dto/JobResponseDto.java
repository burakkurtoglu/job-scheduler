package com.wornexe.job_scheduler.dto;

import java.time.LocalDateTime;

public record JobResponseDto(long id, LocalDateTime nextExecutionTime, LocalDateTime lastExecutionTime) {
}
