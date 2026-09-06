package com.wornexe.job_scheduler.util;

public class HourParser implements ParserRules{

    static final int MIN = 0, MAX = 23;

    @Override
    public String getFieldName(){return "hour";}
    @Override
    public ParsedString parse(String field) {
        return generalParser(field, MIN, MAX);
    }
}
