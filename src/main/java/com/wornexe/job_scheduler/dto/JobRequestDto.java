package com.wornexe.job_scheduler.dto;

public record JobRequestDto(String cronStr, int payload) {
}
