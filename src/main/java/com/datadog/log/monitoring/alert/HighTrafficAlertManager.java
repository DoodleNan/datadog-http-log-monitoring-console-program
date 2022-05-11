package com.datadog.log.monitoring.alert;

import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Optional;

/**
 * Implementation of AlertManager to handle HighTraffic alert. Serve as consumer to consume aggregatedStatisticsCircularFifoQueue queue.
 * The alert result would be further published by HighTrafficAlertWorker.
 *
 * A CircularFifoQueue is used here to keep the moving window size fixed. We compare moving avg to threshold for all stats in the window.
 * It doesn't keep history data as we only store most recent alertsWindowSize amount of traffic stats in queue.
 */
@Slf4j
@AllArgsConstructor
@Getter
public class HighTrafficAlertManager implements AlertManager {
    private static final String ALERT_MESSAGE = "========== A %s alert is now %s ==========\n\n" +
            "\t hits: %d during time window [ %s, %s ]\n\n" +
            "================ End of an alert notification ================\n";
    private static final String HIGH_TRAFFIC_ALERT_NAME = "High Traffic Alert";
    private int alertsWindowSize;
    private int averageRequestsThreshold;
    private int totalTrafficInAlertWindow;
    private CircularFifoQueue<AggregatedStatistics> aggregatedStatisticsCircularFifoQueue;
    private Alert highTrafficAlert;

    public HighTrafficAlertManager(int alertsWindowSize, int averageRequestsThreshold) {
        this.alertsWindowSize = alertsWindowSize;
        this.averageRequestsThreshold = averageRequestsThreshold;
        this.aggregatedStatisticsCircularFifoQueue = new CircularFifoQueue<>(alertsWindowSize);
        this.highTrafficAlert = new Alert(HIGH_TRAFFIC_ALERT_NAME);
    }

    /**
     * Get Alert result given AggregatedStatistics record.
     *
     * @param aggregatedStatistics aggregatedStatistics from aggregatedStatisticsCircularFifoQueue to be consumed and processed by HighTrafficAlertManager
     * @return Alert message if applicable, otherwise empty Optional
     */
    public Optional<String> getAlertResult(@NonNull AggregatedStatistics aggregatedStatistics) {
        aggregatedStatisticsCircularFifoQueue.offer(aggregatedStatistics);

        totalTrafficInAlertWindow += aggregatedStatistics.getTotalRequestCount();

        if (aggregatedStatisticsCircularFifoQueue.isAtFullCapacity()) {
            int averageHitsInAlertWindow = Math.round((float) totalTrafficInAlertWindow / (float) aggregatedStatisticsCircularFifoQueue.maxSize());

            log.info("averageHitsInAlertWindow: {}; window size: {}", averageHitsInAlertWindow, alertsWindowSize);

            totalTrafficInAlertWindow -= aggregatedStatisticsCircularFifoQueue.remove().getTotalRequestCount();


            if (averageHitsInAlertWindow > averageRequestsThreshold) {
                if (!highTrafficAlert.getAlertState().equals(Alert.AlertState.Active)) {
                    highTrafficAlert.setAlertState(Alert.AlertState.Active);

                    return Optional.of(String.format(ALERT_MESSAGE,
                            highTrafficAlert.getAlertName(),
                            highTrafficAlert.getAlertState().name(),
                            aggregatedStatistics.getTotalRequestCount(),
                            aggregatedStatistics.getStartTimestamp(),
                            aggregatedStatistics.getEndTimestamp()));
                }
            }

            if (averageHitsInAlertWindow <= averageRequestsThreshold) {
                if (highTrafficAlert.getAlertState().equals(Alert.AlertState.Active)) {
                    highTrafficAlert.setAlertState(Alert.AlertState.Recovered);

                    return Optional.of(String.format(ALERT_MESSAGE,
                            highTrafficAlert.getAlertName(),
                            highTrafficAlert.getAlertState().name(),
                            aggregatedStatistics.getTotalRequestCount(),
                            aggregatedStatistics.getStartTimestamp(),
                            aggregatedStatistics.getEndTimestamp()));
                }

                if (highTrafficAlert.getAlertState().equals(Alert.AlertState.Recovered)) {
                    highTrafficAlert.setAlertState(Alert.AlertState.Inactive);
                }
            }
        }

        return Optional.empty();
    }
}
