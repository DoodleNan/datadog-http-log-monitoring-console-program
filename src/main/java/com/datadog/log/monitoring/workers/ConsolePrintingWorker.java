package com.datadog.log.monitoring.workers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

/**
 * ConsolePrintingWorker servers as consumer to take input from consoleOutputQueue and print the content in console.
 */
@AllArgsConstructor
@Slf4j
public class ConsolePrintingWorker implements Runnable {
    private BlockingQueue<String> consoleOutputQueue;

    /**
     * Print content from consoleOutputQueue to console.
     */
    @Override
    public void run() {
        log.info("ConsolePrintingWorker starts...");
        try {
            while(true) {
                System.out.println(consoleOutputQueue.take());
            }
        }catch (InterruptedException ex) {
            ex.printStackTrace();
            log.error("Console printing worker interrupted!");
        }
    }

}
