package com.wornexe.job_scheduler.util;


import java.util.HashMap;
import java.util.Map;

public class CronParser {
    ParserRules[] parsers = {new MinuteParser(), new HourParser(), new DayParser(),  new MonthParser(), new WeekdayParser()}; // minute-hour-day-month-weekday, priority is important

    public Map<String, ParserRules.ParsedString> cronParser(String cron){
        String[] splitField = cron.split(" ");
        if(splitField.length != 5){throw new IllegalArgumentException("cron should contain 5 fields");}
        Map<String, ParserRules.ParsedString> result = new HashMap<>();
        for(int i = 0; i < splitField.length; i++){

            result.put(parsers[i].getFieldName(), parsers[i].parse(splitField[i]));
        }

        return result;
    }

}
