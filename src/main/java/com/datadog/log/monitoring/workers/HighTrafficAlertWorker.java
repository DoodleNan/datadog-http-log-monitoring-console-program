package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.alert.AlertManager;
import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.LogLine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * HighTrafficAlertWorker servers as consumer to take content from aggregatedStatisticsBlockingQueue, calculate if alert needs to triggered, and serves as
 * publisher to push the alert to consoleOutputQueue if alert does need to trigger.
 */
@Slf4j
@AllArgsConstructor
public class HighTrafficAlertWorker implements Runnable {
    private BlockingQueue<AggregatedStatistics> aggregatedStatisticsBlockingQueue;
    private BlockingQueue<String> consoleOutputQueue;
    private AlertManager alertManager;

    /**
     * Take input from aggregatedStatisticsBlockingQueue, construct alert if applicable, and push the alert to consoleOutputQueue for printing.
     */
    public void run() {
        log.info("HighTrafficAlertWorker starts...");
        while (true) {
            try {
                Optional<String> alertOutput = alertManager.getAlertResult(aggregatedStatisticsBlockingQueue.take());
                if(alertOutput.isPresent()) {
                    consoleOutputQueue.offer(alertOutput.get());
                }
            } catch (InterruptedException e) {
                log.warn("AlertsMonitorWorker got interrupted", e);
            }
        }
    }
}
