package com.wornexe.job_scheduler.util;


public class MinuteParser implements ParserRules{

    @Override
    public String getFieldName(){return "minute";}

    static final int MIN = 0, MAX = 59;

    @Override
    public ParsedString parse(String field) {
        return generalParser(field, MIN, MAX);
    }
}