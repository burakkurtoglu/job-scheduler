package com.wornexe.job_scheduler.util;

import java.time.LocalDateTime;
import java.util.Arrays;

enum MonthInfo{
    SAME_MONTH, NEXT_MONTH, WRAPPED_TO_NEXT_YEAR
}

public class NextExecutionTime {

    public class ResultMonth{
        public ResultMonth(int month, LocalDateTime ldt, MonthInfo monthInfo){
            this.month = month;
            this.monthInfo = monthInfo;
            this.ldt = ldt;
        }
        int month;

        MonthInfo monthInfo;
        LocalDateTime ldt;
    }

    public ResultMonth isolateMonth(ParserRules.ParsedString monthParsed, LocalDateTime ldt){

        int curMonth = ldt.getMonthValue();
        int[] monthArr = monthParsed.arr;
        int index = Arrays.binarySearch(monthArr, curMonth);
        if(index >= 0){
            ResultMonth rm = new ResultMonth(curMonth, ldt, MonthInfo.SAME_MONTH);
            // alt levels.
            return rm;
        }else{
            int insertionIndex = (-index - 1);
            if (insertionIndex < monthArr.length){
                ldt = ldt.withDayOfMonth(1).withHour(0).withMinute(0).withMonth(monthArr[insertionIndex]);
                ResultMonth rm = new ResultMonth(monthArr[insertionIndex], ldt, MonthInfo.NEXT_MONTH);
                // make year counter++
                // alt levels
                return rm;
            }else{
                ldt = ldt.withDayOfMonth(1).withHour(0).withMinute(0).withMonth(monthArr[0]);
                ResultMonth rm = new ResultMonth(monthArr[0], ldt, MonthInfo.WRAPPED_TO_NEXT_YEAR);
                // make year counter++
                return rm;
            }
        }

    }
    public class ResultDay{
        int day;
        boolean isFound;
        LocalDateTime ldt;
        public ResultDay(int day, boolean isFound, LocalDateTime ldt){
            this.day = day;
            this.isFound = isFound;
            this.ldt = ldt;

        }
    }
    public ResultDay isolateDay(int[] dayResolvedArr, LocalDateTime ldt){
        int curDay = ldt.getDayOfMonth();
        int index = Arrays.binarySearch(dayResolvedArr, curDay);
        if(index >= 0){
            ResultDay rd = new ResultDay(curDay, true, ldt);
            // alt levels
            return rd;
        }else{
            int insertionIndex = (-index - 1);
            if(insertionIndex < dayResolvedArr.length){
                ldt = ldt.withHour(0).withMinute(0).withDayOfMonth(dayResolvedArr[insertionIndex]);
                ResultDay rd = new ResultDay(dayResolvedArr[insertionIndex], true, ldt);
                return rd;
            }else{
                ldt = ldt.withHour(0).withMinute(0).withDayOfMonth(dayResolvedArr[0]);
                ResultDay rd = new ResultDay(dayResolvedArr[0], false, ldt);
                return rd;

            }
        }
    }

    public class ResultHour{
        int hour;
        boolean isFound;
        LocalDateTime ldt;
        public ResultHour(int hour, boolean isFound, LocalDateTime ldt){
            this.hour = hour;
            this.isFound = isFound;
            this.ldt = ldt;

        }
    }

    public ResultHour isolateHour(ParserRules.ParsedString hourParsed, LocalDateTime ldt){
        int curHour = ldt.getHour();
        int index = Arrays.binarySearch(hourParsed.arr, curHour);
        if(index >= 0){
            ResultHour rh = new ResultHour(curHour, true, ldt);
            return rh;
        }else{
            int insertionIndex = (-index - 1);
            if(insertionIndex < hourParsed.arr.length){
                ldt = ldt.withMinute(0).withHour(hourParsed.arr[insertionIndex]);
                ResultHour rh = new ResultHour(hourParsed.arr[insertionIndex], true, ldt);
                return rh;
            }else{
                ldt = ldt.withMinute(0).withHour(hourParsed.arr[0]);
                ResultHour rh = new ResultHour(hourParsed.arr[0], false, ldt);
                return rh;
            }
        }
    }

    public class ResultMinute{
        int minute;
        boolean isFound;
        LocalDateTime ldt;
        public ResultMinute(int minute, boolean isFound, LocalDateTime ldt){
            this.minute = minute;
            this.isFound = isFound;
            this.ldt = ldt;
        }
    }

    public ResultMinute isolateMinute(ParserRules.ParsedString minuteParsed, LocalDateTime ldt){
        int curMinute = ldt.getMinute();
        int index = Arrays.binarySearch(minuteParsed.arr, curMinute);
        if(index >= 0 ){
            ResultMinute rm = new ResultMinute(curMinute, true, ldt);
            return rm;
        }else{
            int insertionIndex = (-index - 1);
            if(insertionIndex < minuteParsed.arr.length){
                ldt = ldt.withMinute(minuteParsed.arr[insertionIndex]);
                ResultMinute rm = new ResultMinute(minuteParsed.arr[insertionIndex], true, ldt);
                return rm;
            }else{
                ldt = ldt.withMinute(minuteParsed.arr[0]);
                ResultMinute rm = new ResultMinute(minuteParsed.arr[0], false, ldt);
                return rm;
            }
        }
    }
    int maxAttempts = 4;
    // LocalDateTime curLdt = LocalDateTime.now();
    public LocalDateTime nextExecutionTime(ParserRules.ParsedString parsedMonth, ParserRules.ParsedString parsedDay,
                                           ParserRules.ParsedString parsedHour, ParserRules.ParsedString parsedMinute, ParserRules.ParsedString parsedWeekday, LocalDateTime curLdt){

        int yearCounter = 0;


        while(yearCounter < maxAttempts){
            ResultMonth resultMonth = isolateMonth(parsedMonth, curLdt);
            if (resultMonth.monthInfo == MonthInfo.WRAPPED_TO_NEXT_YEAR){
                curLdt = resultMonth.ldt;
                curLdt = curLdt.plusYears(1);
                yearCounter++;
            }else{
                    int[] timeField = new int[]{curLdt.getYear(), curLdt.getMonthValue()};
                    int[] dayResolverArr = DayResolver.dayResolver(parsedDay,parsedWeekday, timeField);
                    curLdt = resultMonth.ldt;
                    ResultDay resultDay = isolateDay(dayResolverArr, curLdt);
                    if(!resultDay.isFound){ // try next month
                        curLdt = curLdt.withDayOfMonth(1).withHour(0).withMinute(0).plusMonths(1);
                    }else{ // look for matching hour
                        curLdt = resultDay.ldt;
                        ResultHour resultHour = isolateHour(parsedHour, curLdt);
                        if(!resultHour.isFound){
                            curLdt = resultHour.ldt; // new added
                            curLdt = curLdt.plusDays(1);
                        }else{ // look for matching minute
                            curLdt = resultHour.ldt;
                            ResultMinute resultMinute = isolateMinute(parsedMinute, curLdt);
                            if(!resultMinute.isFound){
                                curLdt = curLdt.plusHours(1);
                            }else{
                                // successfully found nextExecutionTime
                                return resultMinute.ldt;
                            }
                        }
                    }
                }
            }
        // return an exception probably, because there is no match for next execution time
        throw new IllegalArgumentException("no execution time found");

    }

}
