package com.datadog.log.monitoring.parser;

import com.datadog.log.monitoring.exceptions.LogParserException;
import com.datadog.log.monitoring.model.HTTPMethod;
import com.datadog.log.monitoring.model.LogLine;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implement LogParser interface with example log file.
 */
@Slf4j
public class ExampleLogParser implements LogParser {
    // Example input line
    // "10.0.0.1","-","apache",1549574332,"GET /api/user HTTP/1.0",200,1234
    private static final Pattern pattern = Pattern.compile("^\\\"(\\S.*?)\\\",\\\"(\\S.*?)\\\",\\\"(\\S.*?)\\\",(\\d+),\\\"(\\S.*?)\\\",(\\d+),(\\d+)");

    /**
     * Parse input with given regex.
     *
     * @param input input String to be parsed. A line of log
     * @return LogLine data model with values from raw input log
     * @throws LogParserException
     */
    @Override
    public LogLine parseLog(String input) throws LogParserException {
        input = input.trim();

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw new LogParserException("Can not parse logs, please check input file");
        }

        String[] request = matcher.group(5).split(" ");

        return LogLine
                .builder()
                .host(matcher.group(1))
                .authenticationServer(matcher.group(2))
                .authuser(matcher.group(3))
                .epochTime(Long.parseLong(matcher.group(4)))
                .httpMethod(HTTPMethod.getHttpMethod(request[0]).orElse(HTTPMethod.OTHER.OTHER))
                .requestPath(request[1])
                .httpVersion(request[2])
                .statusCode(Integer.parseInt(matcher.group(6)))
                .contentLength(Integer.parseInt(matcher.group(7)))
                .build();
    }
}
