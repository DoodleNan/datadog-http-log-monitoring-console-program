package com.datadog.log.monitoring.config;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.nio.file.Paths;
import java.util.Optional;

/**
 * AppConfig utility class
 * Validate, Construct and Retrieve AppConfig
 * TODO: Separate validation/construct/retrieval AppConfig logics to separate classes. Currently there are all mixed in this class, and the name AppConfigAccessor is confusing.
 */
@UtilityClass
@Slf4j
public class AppConfigAccessor {
    private final Options options;
    private final CommandLineParser parser = new DefaultParser();
    private final String INVALID_ARGUMENT_VALUE = "Invalid %s argument value: %s";

    static {
        options = new Options()
                .addOption(AppConfigOptions.FileLocation.getCliOption())
                .addOption(AppConfigOptions.StatsInterval.getCliOption())
                .addOption(AppConfigOptions.AlertsInterval.getCliOption())
                .addOption(AppConfigOptions.RPSThreshold.getCliOption());
    }

    /**
     * Parses command line input and construct the AppConfig
     *
     * @param args
     * @return On successful parsing and validation returns the ApplicationConfig
     */
    public Optional<AppConfig> constructAppConfig(String []args) {
        Optional<CommandLine> commandLineOptional = parseUserArgs(args);

        if (!commandLineOptional.isPresent()) {
            new HelpFormatter().printHelp("monitor", options, true);

            return Optional.empty();
        }

        return Optional.of(getValidatedAppConfig(commandLineOptional.get()));
    }

    private Optional<CommandLine> parseUserArgs(String []args) {
        try {
            return Optional.of(parser.parse(options, args));
        } catch (ParseException e) {
            log.warn("Failed to parse command line arguments", e);
        }

        return Optional.empty();
    }

    private AppConfig getValidatedAppConfig(CommandLine commandLine) {
        AppConfig appConfig = new AppConfig();

        String logFileLocationOption = commandLine.getOptionValue(AppConfigOptions.FileLocation.getVerboseName());
        if (logFileLocationOption != null) {
            appConfig.setLogFileLocation(getValidatedLogFileLocation(logFileLocationOption));
        }

        String statsIntervalOption = commandLine.getOptionValue(AppConfigOptions.StatsInterval.getVerboseName());
        if (statsIntervalOption != null) {
            appConfig.setStatsDisplayInterval(getValidatedStatsInterval(statsIntervalOption));
        }

        String alertsIntervalOption = commandLine.getOptionValue(AppConfigOptions.AlertsInterval.getVerboseName());
        if (alertsIntervalOption != null) {
            appConfig.setAlertsMonitoringInterval(getValidatedAlertsInterval(alertsIntervalOption, appConfig.getStatsDisplayInterval()));
        }

        String RPSThresholdOption = commandLine.getOptionValue(AppConfigOptions.RPSThreshold.getVerboseName());
        if (RPSThresholdOption != null) {
            appConfig.setRequestsPerSecondThreshold(getValidatedRPSThreshold(RPSThresholdOption));
        }

        return appConfig;
    }

    private String getValidatedLogFileLocation(String logFileLocationOption) {
        if (!Paths.get(logFileLocationOption).toFile().isFile()) {
            throw new IllegalArgumentException(
                    String.format("Specified Log File Location `%s` is invalid, please check again and retry", logFileLocationOption)
            );
        }

        return logFileLocationOption;
    }

    private int getValidatedStatsInterval(String statsIntervalOption) {
        try {
            int statsDisplayInterval = Integer.parseInt(statsIntervalOption);
            if (statsDisplayInterval <= 0) {
                throw new IllegalArgumentException(
                        String.format(INVALID_ARGUMENT_VALUE, AppConfigOptions.StatsInterval.getVerboseName(), statsIntervalOption)
                );
            }

            return statsDisplayInterval;
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(
                    String.format(INVALID_ARGUMENT_VALUE, AppConfigOptions.StatsInterval.getVerboseName(), statsIntervalOption)
            );
        }
    }

    private int getValidatedAlertsInterval(String alertsIntervalOption, int statsDisplayInterval) {
        try {
            int alertsMonitoringInterval = Integer.parseInt(alertsIntervalOption);
            if (alertsMonitoringInterval < statsDisplayInterval) {
                throw new IllegalArgumentException(
                        String.format(
                                "%s cannot be less than %s",
                                AppConfigOptions.AlertsInterval.getVerboseName(),
                                AppConfigOptions.StatsInterval.getVerboseName()
                        )
                );
            }

            return alertsMonitoringInterval;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    String.format(INVALID_ARGUMENT_VALUE, AppConfigOptions.AlertsInterval.getVerboseName(), alertsIntervalOption)
            );
        }
    }

    private int getValidatedRPSThreshold(String RPSThresholdOption) {
        try {
            int RPSThreshold = Integer.parseInt(RPSThresholdOption);
            if (RPSThreshold <= 0) {
                throw new IllegalArgumentException(
                        String.format(INVALID_ARGUMENT_VALUE, AppConfigOptions.RPSThreshold.getVerboseName(), RPSThresholdOption)
                );
            }

            return RPSThreshold;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    String.format(INVALID_ARGUMENT_VALUE, AppConfigOptions.RPSThreshold.getVerboseName(), RPSThresholdOption)
            );
        }
    }
}
