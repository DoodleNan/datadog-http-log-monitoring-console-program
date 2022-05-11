package com.datadog.log.monitoring.alert;
import com.datadog.log.monitoring.model.AggregatedStatistics;

import java.util.Optional;

/**
 * Interface to handle alerts.
 */
public interface AlertManager {
    Optional<String> getAlertResult(AggregatedStatistics aggregatedStatistics);
}
