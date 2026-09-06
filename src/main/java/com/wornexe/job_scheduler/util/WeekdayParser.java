package com.wornexe.job_scheduler.util;

import java.util.Arrays;

public class WeekdayParser implements ParserRules{

    static final int MIN = 0, MAX = 7;

    @Override
    public String getFieldName(){return "weekday";}

    @Override
    public ParsedString parse(String field) {
        ParsedString ps = new ParsedString();
        ps.arr =Arrays.stream(generalParser(field, MIN, MAX).arr).map(n-> n == 7 ? 0:n).toArray();
        ps.containsAsterisk =generalParser(field, MIN, MAX).containsAsterisk;
        return ps;
    }
}
