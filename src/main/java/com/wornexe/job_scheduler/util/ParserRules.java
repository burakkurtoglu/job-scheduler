package com.wornexe.job_scheduler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public interface ParserRules {
    ParsedString parse(String field);

    String getFieldName();

    class ParsedString{
        int[] arr;
        boolean containsAsterisk;
    }

    default ParsedString generalParser(String field, int min, int max){
        ParsedString ps = new ParsedString();
        ps.containsAsterisk = field.equals("*");

        List<Integer> result = new ArrayList<>();

        String[] splitComma = field.split(",");
        for (String noComma : splitComma) {
            if (noComma.contains("/")) {
                String[] splitSlash = noComma.split("/");
                int[] rangeForSlash;
                if (splitSlash[0].equals("*")) {
                    rangeForSlash = CronSyntaxParser.asteriskParser(min, max);
                } else {
                    String[] splitHyphen = splitSlash[0].split("-");
                    if (splitHyphen.length != 2){throw new IllegalArgumentException("For hyphen parsing needed 2 numbers, got one");}
                    int startI = Integer.parseInt(splitHyphen[0]);
                    int endI = Integer.parseInt(splitHyphen[1]);
                    if(Validator.validate(min, max, startI, endI)) {
                        rangeForSlash = CronSyntaxParser.hyphenParser(startI, endI);
                    }else{
                        throw new IllegalArgumentException((String.format("startI (%d) exceeds min(%d) OR endI (%d) exceeds max(%d) or startI > endI",startI, min, endI,max)));
                    }
                }
                if (Validator.validateStep(Integer.parseInt(splitSlash[1]))) {
                    int[] afterSlashParser = CronSyntaxParser.slashParser(rangeForSlash, Integer.parseInt(splitSlash[1]));
                    Arrays.stream(afterSlashParser).forEach(result::add);
                }else{
                    throw new ArithmeticException("0 or less division exception");
                }


            } else {
                if (noComma.contains("-")) {
                    String[] splitHyphen = noComma.split("-");
                    int startI = Integer.parseInt(splitHyphen[0]);
                    int endI = Integer.parseInt(splitHyphen[1]);
                    if(Validator.validate(min, max, startI, endI)) {
                        int[] afterHyphenParser = CronSyntaxParser.hyphenParser(Integer.parseInt(splitHyphen[0]), Integer.parseInt(splitHyphen[1]));
                        Arrays.stream(afterHyphenParser).forEach(result::add);
                    }else{
                        throw new IllegalArgumentException((String.format("startI (%d) exceeds min(%d) OR endI (%d) exceeds max(%d) or startI > endI",startI, min, endI,max)));
                    }
                } else {
                    if (noComma.equals("*")) {
                        int[] afterAsteriskParser = CronSyntaxParser.asteriskParser(min, max);
                        Arrays.stream(afterAsteriskParser).forEach(result::add);
                    } else {
                        int numb = Integer.parseInt(noComma);
                        if(Validator.validate(min, max, numb, numb)) {
                            result.add(numb);
                        }
                        else{throw new IllegalArgumentException(String.format("number %d exceeds limits of min (%d) or max (%d)", numb, min, max));}
                    }
                }
            }
        }
        ps.arr = result.stream().mapToInt(Integer::intValue).sorted().toArray();
        return ps;
    }
}
