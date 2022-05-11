package com.datadog.log.monitoring.workers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsolePrintingWorkerTest {
    private static ConsolePrintingWorker consolePrintingWorker;
    private static BlockingQueue<String> consoleOutputQueue;
    private static int queueSize = 10;

    @BeforeAll
    static void init() {
        consoleOutputQueue = new LinkedBlockingQueue<>();
        consolePrintingWorker = new ConsolePrintingWorker(consoleOutputQueue);
        fillQueue(queueSize);
    }

    @Test
    public void testRun() {
        // TODO: Figure out a way to test while true loop
//        consolePrintingWorker.run();

    }

    private static void fillQueue(int size) {
        for(int i = 0; i < size; i++) {
            consoleOutputQueue.offer("dummy-console-output");
        }
    }
}
