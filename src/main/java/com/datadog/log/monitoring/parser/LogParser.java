package com.datadog.log.monitoring.parser;

import com.datadog.log.monitoring.exceptions.LogParserException;
import com.datadog.log.monitoring.model.LogLine;

/**
 * Interface to parse Log.
 */
public interface LogParser {
    LogLine parseLog(String input) throws LogParserException;
}
