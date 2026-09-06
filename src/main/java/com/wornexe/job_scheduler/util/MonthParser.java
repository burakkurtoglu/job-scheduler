package com.wornexe.job_scheduler.util;

public class MonthParser implements ParserRules{

    static final int MIN = 1, MAX = 12;

    @Override
    public String getFieldName(){return "month";}

    @Override
    public ParsedString parse(String field) {
        return generalParser(field, MIN, MAX);
    }
}
