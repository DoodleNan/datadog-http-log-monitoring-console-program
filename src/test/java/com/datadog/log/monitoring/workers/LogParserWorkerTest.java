package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.model.LogLine;
import com.datadog.log.monitoring.parser.ExampleLogParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogParserWorkerTest {
    private static LogParserWorker logParserWorker;
    private static ExampleLogParser exampleLogParser;
    private static BlockingQueue<String> rawLogQueue;
    private static BlockingQueue<List<LogLine>> parsedLogLineQueue;
    private static int rawLogQueueSize = 10;
    private static String rawLog = "rawLog";

    @BeforeAll
    public static void init() {
        rawLogQueue = new LinkedBlockingQueue<>();
        parsedLogLineQueue = new LinkedBlockingQueue<>();
        fillRawLogQueue(rawLogQueueSize);
        exampleLogParser = new ExampleLogParser();

        logParserWorker = new LogParserWorker(rawLogQueue, parsedLogLineQueue);

    }

    @Test
    public void testRun() {
//        logParserWorker.run();
    }

    private static void fillRawLogQueue(int size) {
        for(int i = 0; i < size; i++) {
            rawLogQueue.offer(rawLog);
        }
    }

}
