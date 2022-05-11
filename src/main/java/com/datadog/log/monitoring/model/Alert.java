package com.datadog.log.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Enum data model to represent Alert
 */
@Data
@AllArgsConstructor
public class Alert {
    private String AlertName;
    private AlertState alertState;

    public Alert(String alertName) {
        this.AlertName = alertName;
        this.alertState = AlertState.Inactive;
    }

    public enum AlertState {
        Active,
        Inactive,
        Recovered,
    }
}
