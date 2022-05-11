package com.datadog.log.monitoring.workers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

public class RawLogProducerWorkerTest {
    private static RawLogProducerWorker rawLogProducerWorker;
    private static final String toFileName = "src/test/resources/rawLogs.txt";
    private static final String fromFileName = "src/test/resources/sampleRawLogs.txt";
    @BeforeAll
    public static void init() {
        rawLogProducerWorker = new RawLogProducerWorker(fromFileName, toFileName);
    }

    @Test
    public void testRun() throws IOException {
        rawLogProducerWorker.run();
        new FileWriter(toFileName, false).close();
    }
}
