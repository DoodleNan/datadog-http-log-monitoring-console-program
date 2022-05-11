package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.LogLine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * LogStatisticsWorker servers as consumer to take content from parsedLogLineQueue, perform analysis on it, servers as producer to
 * push the results to aggregatedStatisticsQueue and consoleOutputQueue
 */
@Slf4j
@AllArgsConstructor
public class LogStatisticsWorker implements Runnable {
    private BlockingQueue<List<LogLine>> parsedLogLineQueue;
    private BlockingQueue<AggregatedStatistics> aggregatedStatisticsQueue;
    private BlockingQueue<String> consoleOutputQueue;

    /**
     * Poll content from parsedLogLineQueue, construct aggregatedStatistics and push to aggregatedStatisticsQueue and aggregatedStatisticsQueue
     * for downstream consumers.
     */
    public void run() {
        log.info("LogStatisticsWorker starts...");

        while (true) {
            try {
                AggregatedStatistics aggregatedStatistics = getAggregatedStatistics(parsedLogLineQueue.take());
                aggregatedStatisticsQueue.offer(aggregatedStatistics);
                consoleOutputQueue.offer(aggregatedStatistics.toString());
            } catch (InterruptedException e) {
                log.warn("LogStatisticsWorker got interrupted", e);
            }
        }
    }

    private AggregatedStatistics getAggregatedStatistics(List<LogLine> logLines) {
        AggregatedStatistics aggregatedStatistics = new AggregatedStatistics();
        Long startTime = Long.MAX_VALUE;
        Long endTime = Long.MIN_VALUE;
        for (LogLine logLine: logLines) {
            aggregatedStatistics.incrementRequestCount();
            aggregatedStatistics.incrementRequestContentSize(logLine.getContentLength());

            String [] sectionParts = logLine.getRequestPath().split("/");
            String section = "/";

            if (sectionParts.length > 1) {
                section = sectionParts[1];
            }

            startTime = Math.min(startTime, logLine.getEpochTime());
            endTime = Math.max(endTime, logLine.getEpochTime());
            aggregatedStatistics.getHttpMethodsHitsMap().compute(logLine.getHttpMethod(), (k, v) -> v == null ? 1: v+1);

            aggregatedStatistics.getSectionHitsMap().compute(section, (k, v) -> v == null ? 1: v+1);
        }

        aggregatedStatistics.setStartTimestamp(startTime);
        aggregatedStatistics.setEndTimestamp(endTime);

        return aggregatedStatistics;
    }

}
