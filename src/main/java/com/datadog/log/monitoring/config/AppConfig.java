package com.datadog.log.monitoring.config;

import lombok.Data;

/**
 * Data model for AppConfig. Can be overridden by command line input
 */
@Data
public class AppConfig {
    // location from which the logs will be tailed
    private String logFileLocation = "rawLogs.txt";

    // location for generate logs
    private String logFileSample = "sample_csv.txt";

    // In seconds
    private int statsDisplayInterval = 10;

    // In seconds
    private int alertsMonitoringInterval = 120;

    // Max RPS after which we will fire alert
    private int requestsPerSecondThreshold = 10;
}
