package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.alert.AlertManager;
import com.datadog.log.monitoring.alert.HighTrafficAlertManager;
import com.datadog.log.monitoring.model.AggregatedStatistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HighTrafficAlertWorkerTest {
    private static HighTrafficAlertWorker highTrafficAlertWorker;
    private static BlockingQueue<AggregatedStatistics> aggregatedStatisticsBlockingQueue;
    private static BlockingQueue<String> consoleOutputQueue;
    private static AlertManager alertManager;
    private static int aggregatedStatisticsBlockingQueueSize = 10;
    private static int alertWindow = 12;
    private static int alertThreshold = 10;


    @BeforeAll
    public static void init() {
        aggregatedStatisticsBlockingQueue = new LinkedBlockingQueue<>();
        fillAggregatedStatisticsBlockingQueue(aggregatedStatisticsBlockingQueueSize);
        consoleOutputQueue = new LinkedBlockingQueue<>();
        alertManager = new HighTrafficAlertManager(alertWindow, alertThreshold);
        highTrafficAlertWorker = new HighTrafficAlertWorker(aggregatedStatisticsBlockingQueue, consoleOutputQueue, alertManager);
    }

    @Test
    public void testRun() {

//        highTrafficAlertWorker.run();
    }

    private static void fillAggregatedStatisticsBlockingQueue(int size) {
        for(int i = 0; i < size; i++) {
            aggregatedStatisticsBlockingQueue.offer(new AggregatedStatistics());
        }
    }
}
