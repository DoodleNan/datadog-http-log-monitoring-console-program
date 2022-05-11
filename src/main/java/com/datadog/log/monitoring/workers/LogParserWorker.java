package com.datadog.log.monitoring.workers;

import com.datadog.log.monitoring.model.LogLine;
import com.datadog.log.monitoring.parser.ExampleLogParser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * LogParserWorker serves as consumer to take content from rawLogQueue, apply the parsing logic and serves as producer to publish parsed logs to parsedLogLineQueue
 */
@Slf4j
public class LogParserWorker implements Runnable {
    private ExampleLogParser exampleLogParser;
    private BlockingQueue<String> rawLogQueue;
    private BlockingQueue<List<LogLine>> parsedLogLineQueue;

    public LogParserWorker(BlockingQueue<String> rawLogQueue, BlockingQueue<List<LogLine>> parsedLogLineQueue) {
        this.rawLogQueue = rawLogQueue;
        this.parsedLogLineQueue = parsedLogLineQueue;
        this.exampleLogParser = new ExampleLogParser();
    }

    /**
     * Poll content from rawLogQueue, parse the content and publish LogLine domain data to parsedLogLineQueue.
     */
    public void run() {
        log.info("LogParser starts...");
        try {
            List<String> rawLogLines = new ArrayList<>();
            rawLogLines.add(rawLogQueue.take());
            rawLogQueue.drainTo(rawLogLines, rawLogQueue.size());
            List<LogLine> parsedLogLines = new LinkedList<>();
            for(String rawLogLine: rawLogLines) {
                parsedLogLines.add(parseLogLine(rawLogLine));
            }
            parsedLogLineQueue.offer(parsedLogLines);

        } catch (InterruptedException ex) {
            log.error("LogParserWorker: Encounter exception during log parser task.");
            ex.printStackTrace();
        }
    }

    private LogLine parseLogLine(String rawLogLine) {
        return exampleLogParser.parseLog(rawLogLine);
    }
}
