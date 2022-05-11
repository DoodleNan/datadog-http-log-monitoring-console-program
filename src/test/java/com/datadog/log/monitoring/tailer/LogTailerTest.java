package com.datadog.log.monitoring.tailer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogTailerTest {
    private static LogTailer logTailer;
    private static BlockingQueue<String> rawLogQueue;
    private static final String rawLog = "dummy-log";

    @BeforeAll
    public static void init() {
        rawLogQueue = new LinkedBlockingQueue<>();
        logTailer = new LogTailer(rawLogQueue);
    }

    @Test
    public void testHandle(){
        logTailer.handle(rawLog);
    }
}
