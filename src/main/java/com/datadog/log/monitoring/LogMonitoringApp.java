package com.datadog.log.monitoring;

import com.datadog.log.monitoring.alert.HighTrafficAlertManager;
import com.datadog.log.monitoring.config.AppConfig;
import com.datadog.log.monitoring.config.AppConfigAccessor;
import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.LogLine;
import com.datadog.log.monitoring.tailer.LogTailer;
import com.datadog.log.monitoring.workers.ConsolePrintingWorker;
import com.datadog.log.monitoring.workers.HighTrafficAlertWorker;
import com.datadog.log.monitoring.workers.LogParserWorker;
import com.datadog.log.monitoring.workers.LogStatisticsWorker;
import com.datadog.log.monitoring.workers.RawLogProducerWorker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LogMonitoringApp {
    public static void main(String[] args) {
        Optional<AppConfig> appConfigOptional = AppConfigAccessor.constructAppConfig(args);
        if (!appConfigOptional.isPresent()) {
            System.exit(1);
        }

        startWorkers(appConfigOptional.get());
    }

    private static void startWorkers(AppConfig appConfig) {
        log.info("starting workers");

        // Queue to keep incoming log lines by tail -f
        BlockingQueue<String> rawLogQueue = new LinkedBlockingQueue<>();
        // Queue to keep parsed log lines
        BlockingQueue<List<LogLine>> parsedLogLineQueue = new LinkedBlockingQueue<>();
        // Queue to keep
        BlockingQueue<AggregatedStatistics> aggregatedStatisticsQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> consoleOutputQueue = new LinkedBlockingQueue<>();

        // These executors will not be shutdown, unless the user closes the application or the main thread dies.
        // If due to some reason any of the executors gets interrupted, they will be restarted.
        ScheduledExecutorService scheduledLogsWorker = Executors.newSingleThreadScheduledExecutor();
        ExecutorService executableWorkers = Executors.newFixedThreadPool(5);

        try {
            // Log writer
            executableWorkers.submit(new RawLogProducerWorker(appConfig.getLogFileSample(), appConfig.getLogFileLocation()));

            // worker which tails the log file for any new events.
            executableWorkers.submit(new Tailer(
                    Paths.get(appConfig.getLogFileLocation()).toFile(),
                    new LogTailer(rawLogQueue),
                    0,
                    true)
            );

            // scheduled worker for receiving tailed logs and parsing them.
            scheduledLogsWorker.scheduleAtFixedRate(
                    new LogParserWorker(rawLogQueue, parsedLogLineQueue),
                    appConfig.getStatsDisplayInterval(), // 10s
                    appConfig.getStatsDisplayInterval(),
                    TimeUnit.SECONDS
            );

            // worker consuming parsed log lines and produces the aggregated Statistics for the log lines received.
            executableWorkers.submit(new LogStatisticsWorker(
                    parsedLogLineQueue,
                    aggregatedStatisticsQueue,
                    consoleOutputQueue)
            );

            // worker which consumes traffic summary and monitors for any possible alerts.
            executableWorkers.submit(new HighTrafficAlertWorker(
                    aggregatedStatisticsQueue,
                    consoleOutputQueue,
                    new HighTrafficAlertManager(
                        appConfig.getAlertsMonitoringInterval() / appConfig.getStatsDisplayInterval(), // 120s / 10s = 12
                            appConfig.getRequestsPerSecondThreshold()
                    )
            ));

            // Worker printing results in console
            executableWorkers.submit(new ConsolePrintingWorker(consoleOutputQueue));
        } catch (RejectedExecutionException executionException) {
            log.error("Could not start one of the workers, run the application again", executionException);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @SneakyThrows
            public void run() {
                new FileWriter(appConfig.getLogFileLocation(), false).close();
            }
        });
    }
}
