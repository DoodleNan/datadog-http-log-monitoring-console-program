package com.datadog.log.monitoring.alert;

import com.datadog.log.monitoring.model.AggregatedStatistics;
import com.datadog.log.monitoring.model.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HighTrafficAlertManagerTest {
    private HighTrafficAlertManager highTrafficAlertsManager;

    private static final int ALERTS_WINDOW_SIZE = 5;
    private static final int ALERTS_THRESHOLD = 10;
    private static final AggregatedStatistics aggregatedStatistics = new AggregatedStatistics();

    @BeforeEach
    public void init() {
        // Setup alerts monitor with window size of 5 stats and threshold of 10 requests per second
        highTrafficAlertsManager = new HighTrafficAlertManager(ALERTS_WINDOW_SIZE, ALERTS_THRESHOLD);
    }

    @Test
    public void testGetAlert_WindowIsNotFull() {
        offerAggregatedStatistics(ALERTS_WINDOW_SIZE-1, ALERTS_THRESHOLD);

        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Inactive);
    }

    @Test
    public void testGetAlert_WindowIsFull() {
        Optional<String> processAlertResult;

        offerAggregatedStatistics(ALERTS_WINDOW_SIZE, ALERTS_THRESHOLD);
        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Inactive);

        processAlertResult = highTrafficAlertsManager.getAlertResult(new AggregatedStatistics(ALERTS_THRESHOLD + 100));
        assertTrue(processAlertResult.isPresent());
    }

    @Test
    public void testGetAlert_IsRecovered() {
        Optional<String> processAlertResult;
        offerAggregatedStatistics(ALERTS_WINDOW_SIZE, ALERTS_THRESHOLD + 1);

        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Active);

        processAlertResult = highTrafficAlertsManager.getAlertResult(new AggregatedStatistics(0));
        assertTrue(processAlertResult.isPresent());
        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Recovered);
    }

    @Test
    // Tests the below state transition
    // Active -> Recovered -> Inactive
    public void testGetAlert_IsInActiveFromRecovered() {
        Optional<String> processAlertResult;
        offerAggregatedStatistics(ALERTS_WINDOW_SIZE, ALERTS_THRESHOLD + 1);
        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Active);

        processAlertResult = highTrafficAlertsManager.getAlertResult(new AggregatedStatistics(0));
        assertTrue(processAlertResult.isPresent());
        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Recovered);

        processAlertResult = highTrafficAlertsManager.getAlertResult(new AggregatedStatistics(0));
        assertTrue(!processAlertResult.isPresent());
        assertEquals(highTrafficAlertsManager.getHighTrafficAlert().getAlertState(), Alert.AlertState.Inactive);
    }

    private void offerAggregatedStatistics(int aggregatedStatisticsCount, int requestCountPerAggregatedStatistics) {
        for (int i = 0; i < aggregatedStatisticsCount; i++) {
            highTrafficAlertsManager.getAlertResult(new AggregatedStatistics(requestCountPerAggregatedStatistics));
        }
    }
}
