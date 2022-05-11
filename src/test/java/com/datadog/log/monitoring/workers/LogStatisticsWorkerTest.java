package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.LogLine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogStatisticsWorkerTest {
    private static LogStatisticsWorker logStatisticsWorker;
    private static BlockingQueue<List<LogLine>> parsedLogLineQueue;
    private static BlockingQueue<AggregatedStatistics> aggregatedStatisticsQueue;
    private static BlockingQueue<String> consoleOutputQueue;

    @BeforeAll
    public static void init() {
        parsedLogLineQueue = new LinkedBlockingQueue<>();
        aggregatedStatisticsQueue = new LinkedBlockingQueue<>();
        consoleOutputQueue = new LinkedBlockingQueue<>();

        logStatisticsWorker = new LogStatisticsWorker(parsedLogLineQueue, aggregatedStatisticsQueue, consoleOutputQueue);
    }

    @Test
    public void testRun() {
        // TODO: Figure out a way to test while(true) loop
//        logStatisticsWorker.run();
    }
}
