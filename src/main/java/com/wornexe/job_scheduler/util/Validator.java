package com.wornexe.job_scheduler.util;

public class Validator {
    static public boolean validate(int min, int max, int startI, int endI){
        if(min > startI || max < endI || startI > endI){
            return false;
        }
        return true;

    }

    static public boolean validateStep(int step){
        if (step<=0) {
            return false;
        }
        return true;
    }
}
