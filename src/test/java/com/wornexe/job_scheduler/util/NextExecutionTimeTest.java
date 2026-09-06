package com.wornexe.job_scheduler.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

public class NextExecutionTimeTest {
    @Test
    void testNextExecutionTimeHappyPath(){
        LocalDateTime referenceLdt = LocalDateTime.of(2026, 3, 10, 14, 22);
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> result = cp.cronParser("30 15 * * *");
        NextExecutionTime net = new NextExecutionTime();
        LocalDateTime netResult = net.nextExecutionTime(result.get("month"), result.get("day"), result.get("hour"), result.get("minute"), result.get("weekday"), referenceLdt);
        assertEquals(LocalDateTime.of(2026, 3, 10, 15, 30), netResult, "unexpected mismatch");
    }

    @Test
    void testNextExecutionTimeNextMonth(){
        LocalDateTime referenceLdt = LocalDateTime.of(2026, 3, 10, 10, 0);
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> result = cp.cronParser("0 0 1 6,9 *");

        NextExecutionTime net = new NextExecutionTime();
        LocalDateTime netResult = net.nextExecutionTime(result.get("month"), result.get("day"), result.get("hour"), result.get("minute"), result.get("weekday"), referenceLdt);
        assertEquals(LocalDateTime.of(2026, 6, 1, 0, 0), netResult, "unexpected mismatch");
    }

    @Test
    void testNextExecutionTimeSuccessfulWrap(){
        LocalDateTime referenceLdt = LocalDateTime.of(2026, 11, 10, 8, 0);
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> result = cp.cronParser("0 0 1 3 *");

        NextExecutionTime net = new NextExecutionTime();
        LocalDateTime netResult = net.nextExecutionTime(result.get("month"), result.get("day"), result.get("hour"), result.get("minute"), result.get("weekday"), referenceLdt);
        assertEquals(LocalDateTime.of(2027, 3, 1, 0, 0), netResult, "unexpected mismatch");
    }

    @Test
    void testNextExecutionTimeUnsuccessfulWrap(){
        LocalDateTime referenceLdt = LocalDateTime.of(2024, 3, 1, 0, 0);
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> result = cp.cronParser("0 0 29 2 *");
        NextExecutionTime net = new NextExecutionTime();
        assertThrows(RuntimeException.class, () -> net.nextExecutionTime(result.get("month"), result.get("day"), result.get("hour"), result.get("minute"), result.get("weekday"), referenceLdt));
    }

    @Test
    void testNextExecutionTimeNextDay(){
        LocalDateTime referenceLdt = LocalDateTime.of(2026, 3, 10, 23, 45);
        CronParser cp = new CronParser();
        Map<String, ParserRules.ParsedString> result = cp.cronParser("0 22 * * *");
        NextExecutionTime net = new NextExecutionTime();
        LocalDateTime netResult = net.nextExecutionTime(result.get("month"), result.get("day"), result.get("hour"), result.get("minute"), result.get("weekday"), referenceLdt);
        assertEquals(LocalDateTime.of(2026, 3, 11, 22, 0), netResult, "unexpected mismatch");
    }

}
