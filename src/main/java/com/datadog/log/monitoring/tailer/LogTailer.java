package com.datadog.log.monitoring.tailer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.concurrent.BlockingQueue;

/**
 * Similar to tail -f command, tail/listen log files and push to raw logs into the queue
 */
@Slf4j
@AllArgsConstructor
public class LogTailer extends TailerListenerAdapter {
    private BlockingQueue<String> rawLogQueue;

    public void handle(String line) {
        rawLogQueue.offer(line);
    }

    public void fileNotFound() {
        log.error("Log file not found");
    }

    public void handle(Exception ex) { ex.printStackTrace(); }

}
