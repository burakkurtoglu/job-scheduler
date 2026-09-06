package com.wornexe.job_scheduler.util;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DayResolver {

    static public int[] dayResolver(ParserRules.ParsedString dayOfMonth, ParserRules.ParsedString dayOfWeek, int[] timeField){
        java.time.Year year = Year.of(timeField[0]); // YEAR
         java.time.Month month = Month.of(timeField[1]); // MONTH
        int monthLength = month.length(year.isLeap());
        List<Integer> result = new ArrayList<>();
        for(int day = 1; day <= monthLength; day++){
            LocalDate date = LocalDate.of(year.getValue(), month, day);
            int possixWeekday = date.getDayOfWeek().getValue();
            if(possixWeekday == 7){possixWeekday=0;}
            int finalPosixWeekday = possixWeekday;
            int finalDay = day;
            if(dayOfMonth.containsAsterisk || dayOfWeek.containsAsterisk){
                if(!dayOfWeek.containsAsterisk && Arrays.stream(dayOfWeek.arr).anyMatch(x -> x == finalPosixWeekday)){
                    result.add(finalDay);
                }
                if(dayOfWeek.containsAsterisk && !dayOfMonth.containsAsterisk && Arrays.stream(dayOfMonth.arr).anyMatch(x->x == finalDay)){
                    result.add(finalDay);
                }
                if(dayOfMonth.containsAsterisk && dayOfWeek.containsAsterisk){
                    result.add(finalDay);
                }

            } else{

                if(Arrays.stream(dayOfWeek.arr).anyMatch(x -> x == finalPosixWeekday) || Arrays.stream(dayOfMonth.arr).anyMatch(x->x == finalDay)){result.add(finalDay);}

            }

        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }


}
