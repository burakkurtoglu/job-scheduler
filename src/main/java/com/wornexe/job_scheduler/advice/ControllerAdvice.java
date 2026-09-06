package com.wornexe.job_scheduler.advice;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadCronStr(IllegalArgumentException e){

        return ResponseEntity.badRequest().body("bad cron string " + e.getMessage());
    }
}
