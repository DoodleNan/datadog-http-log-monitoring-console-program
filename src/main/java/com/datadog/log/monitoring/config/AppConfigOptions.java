package com.datadog.log.monitoring.config;

import lombok.Getter;
import org.apache.commons.cli.Option;

/**
 * Enum class to represent AppConfig Options, input from command line
 */
public enum AppConfigOptions {
    FileLocation("f", "file-location", "location of the log file"),
    StatsInterval("s", "stats-interval", "time interval after which stats will be displayed"),
    AlertsInterval("a", "alerts-interval", "length of the time window for monitoring the alerts"),
    RPSThreshold("r", "max-request-per-sec", "maximum requests per second threshold, after which alert will fire");

    @Getter
    private final String shortName;
    @Getter
    private final String verboseName;
    @Getter
    private final String description;

    AppConfigOptions(String shortName, String verboseName, String description) {
        this.shortName = shortName;
        this.verboseName = verboseName;
        this.description = description;
    }

    public Option getCliOption() {
        return new Option(
                this.getShortName(),
                this.getVerboseName(),
                true,
                this.getDescription()
        );
    }
}
