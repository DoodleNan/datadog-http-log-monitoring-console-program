package com.datadog.log.monitoring.workers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * RawLogProducerWorker generates log files. Ideally the log files should be dynamically generated by some program so that our application can tail and produce the
 * stats. I use this RawLogProducerWorker to copy from example input one line per sec and put into input location
 */
@Slf4j
@AllArgsConstructor
public class RawLogProducerWorker implements Runnable{
    private final String fromFileName;
    private final String toFileName;

    /**
     * Copy log items from example csv and write to input log file location.
     */
    public void run() {

        while(true) {
            try {
                FileReader fr = new FileReader(fromFileName);
                FileWriter fw = new FileWriter(toFileName);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while((line = br.readLine()) != null) {
                    fw.write(line);
                    fw.write("\n");
                    Thread.sleep(100);
                }

                fr.close();
                fw.close();
                break;
            } catch (IOException  | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
