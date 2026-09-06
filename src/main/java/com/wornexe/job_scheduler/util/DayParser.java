package com.wornexe.job_scheduler.util;

public class DayParser implements ParserRules{

    static final int MIN = 1, MAX = 31;

    @Override
    public String getFieldName(){return "day";}

    @Override
    public ParsedString parse(String field) {
        return generalParser(field, MIN, MAX);
    }
}
