package com.wornexe.job_scheduler.util;


import java.util.Arrays;
import java.util.stream.IntStream;

public class CronSyntaxParser {
    static int[] asteriskParser(int startI, int endI){
        return IntStream.rangeClosed(startI,endI).toArray();
    }

    static int[] hyphenParser(int startI, int endI){
        return IntStream.rangeClosed(startI, endI).toArray();

    }

    static int[] slashParser(int[] range, int step){
        int[] filteredArray = Arrays.stream(range).filter(n-> (n-range[0]) % step == 0).toArray();
        return filteredArray;
    }
 }
