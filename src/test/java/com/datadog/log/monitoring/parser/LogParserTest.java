package com.datadog.log.monitoring.parser;

import com.datadog.log.monitoring.model.HTTPMethod;
import com.datadog.log.monitoring.model.LogLine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class LogParserTest {
    private static ExampleLogParser exampleLogParser;
    private static final String validInput = "\"10.0.0.1\",\"-\",\"apache\",1549574332,\"GET /api/user HTTP/1.0\",200,1234";

    @BeforeAll
    public static void init() {
        exampleLogParser = new ExampleLogParser();
    }

    @Test
    public void testParseLog() {
        LogLine logLine = exampleLogParser.parseLog(validInput);
        assertEquals(logLine.getEpochTime(), 1549574332L);
        assertEquals(logLine.getHost(),"10.0.0.1");
        assertEquals(logLine.getAuthenticationServer(), "-");
        assertEquals(logLine.getHttpMethod(), HTTPMethod.GET);
        assertEquals(logLine.getContentLength(), 1234);
        assertEquals(logLine.getRequestPath(), "/api/user");
        assertEquals(logLine.getAuthuser(), "apache");
        assertEquals(logLine.getStatusCode(), 200);
    }
}
