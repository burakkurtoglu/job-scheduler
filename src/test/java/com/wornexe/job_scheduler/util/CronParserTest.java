package com.wornexe.job_scheduler.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CronParserTest {
/*
    @Test
    void testCronSyntaxParser(){
        CronSyntaxParser csp = new CronSyntaxParser();
        assertEquals(60, csp.asteriskParser(0,59).length, String.format("expected 60 got %d", csp.asteriskParser(0, 59).length));
        assertEquals(12, csp.asteriskParser(1,12).length, String.format("expected 12 got %d", csp.asteriskParser(1, 12).length));
        int[] result = csp.hyphenParser(5,10);
        assertArrayEquals(new int[]{5,6,7,8,9,10}, result, "unexpected mismatch");
        result = csp.hyphenParser(5,5);
        assertArrayEquals(new int[]{5}, result, "unexpected mismatch");
        result = csp.slashParser(new int[]{5,6,7,8,9,10}, 2);
        assertArrayEquals(new int[]{5,7,9}, result, "unexpected mismatch");
        result = csp.slashParser(new int[]{7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22},5);
        assertArrayEquals(new int[]{7,12,17,22}, result, "unexpected mismatch");
    }
*/
    @Test
    void testValidator(){
        assertTrue(Validator.validate(0,59,10,20), "unexpected false");
        assertTrue(Validator.validate(0,59,0,59), "unexpected false");
        assertFalse(Validator.validate(0,59,20,10), "unexpected true");
        assertFalse(Validator.validate(0,59,-1,10), "unexpected true");
        assertFalse(Validator.validate(0,59,20,100), "unexpected true");
        assertTrue(Validator.validateStep(5), "unexpected false");
        assertFalse(Validator.validateStep(0), "unexpected true");
        assertFalse(Validator.validateStep(-3), "unexpected true");

    }

    @Test
    void testMinuteParser(){
        MinuteParser mp = new MinuteParser();
        //CronSyntaxParser csp = new CronSyntaxParser();

        //assertArrayEquals(new int[]{30}, mp.parse("30"), "unexpected mismatch");
        //assertArrayEquals(new int[]{1,5,10}, mp.parse("1,5,10"), "unexpected mismatch");
        int[] nums = new int[60];
        Arrays.setAll(nums, n ->n);
       //assertArrayEquals(nums, mp.parse("*"), "unexpected mismatch 3");
        //assertArrayEquals(new int[]{0, 15, 30, 45}, mp.parse("*/15"), "unexpected mismatch");
        //assertArrayEquals(new int[]{5,10,15,20,25,30}, mp.parse("5-30/5"), "unexpected mismatch");
        //assertArrayEquals(new int[]{0,1,5,6,7,8,9,10,20,40}, mp.parse("1,5-10,*/20"), String.format("%d", mp.parse("1,5-10/5")[0]));
        //assertThrows(IllegalArgumentException.class, () -> mp.parse("50-10"));
        //assertThrows(ArithmeticException.class, () -> mp.parse("*/0"));
        //assertThrows(IllegalArgumentException.class, () -> mp.parse("2/5"));
    }

    @Test
    void testWeekDayParser(){
        WeekdayParser wdp = new WeekdayParser();
        //CronSyntaxParser csp = new CronSyntaxParser();
        //wdp.setCsp(csp);
        //int[] res = wdp.parse("7");
        //assertArrayEquals(new int[]{0}, res, "unexpected mismatch");
    }

    @Test
    void testCronParser(){
        CronParser cp = new CronParser();
        //int[][] res = cp.cronParser("0,30 9-17 * * 1-5");
        //assertArrayEquals(new int[]{0,30}, res[0]);
        //assertArrayEquals(new int[]{9,10,11,12,13,14,15,16,17}, res[1]);
        //assertArrayEquals(new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}, res[2]);
        //assertArrayEquals(new int[]{1,2,3,4,5,6,7,8,9,10,11,12}, res[3]);
        //assertArrayEquals(new int[]{1,2,3,4,5}, res[4]);
       // assertThrows(IllegalArgumentException.class, () -> cp.cronParser("* * * *"));
        //assertThrows(IllegalArgumentException.class, () -> cp.cronParser("* * * * * *"));

    }

}
