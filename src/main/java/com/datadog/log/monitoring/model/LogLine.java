package com.datadog.log.monitoring.model;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Enum data model to represent log line.
 */
@Data
@Builder
public class LogLine {
    private String host;

    private String authenticationServer;

    private String authuser;

    private Long epochTime;

    private HTTPMethod httpMethod;

    private String requestPath;

    private int statusCode;

    private String httpVersion;

    private int contentLength;
}
