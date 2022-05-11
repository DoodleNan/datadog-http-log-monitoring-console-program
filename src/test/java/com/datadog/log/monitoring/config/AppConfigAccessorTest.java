package com.datadog.log.monitoring.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class AppConfigAccessorTest {
    final String fileLocation = "rawLogs.txt";
    String []args = new String[] { "-r", "10", "-s", "5", "-a", "7", "-f", fileLocation };

    @Test
    public void testValidUserProvidedArgs() {
        Optional<AppConfig> configOptional = AppConfigAccessor.constructAppConfig(args);
        Assertions.assertTrue(configOptional.isPresent());

        AppConfig applicationConfig = configOptional.get();
        Assertions.assertEquals(10, applicationConfig.getRequestsPerSecondThreshold());
        Assertions.assertEquals(5, applicationConfig.getStatsDisplayInterval());
        Assertions.assertEquals(7, applicationConfig.getAlertsMonitoringInterval());
        Assertions.assertEquals(fileLocation, applicationConfig.getLogFileLocation());
    }

}
