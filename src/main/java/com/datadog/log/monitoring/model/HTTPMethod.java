package com.datadog.log.monitoring.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum data model to represent HTTP Methods
 */
public enum HTTPMethod {
    GET,
    PUT,
    POST,
    DELETE,
    OPTIONS,
    HEAD,
    TRACE,
    OTHER;

    public static Optional<HTTPMethod> getHttpMethod(String methodName) {
        return Arrays.stream(HTTPMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(methodName))
                .findFirst();
    }
}
