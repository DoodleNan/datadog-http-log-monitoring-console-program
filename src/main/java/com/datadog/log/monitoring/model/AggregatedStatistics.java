package com.datadog.log.monitoring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Data model for AggregatedStatistics, which keeps Aggregated stats for a time frame.
 */
@NoArgsConstructor
@Getter
@Setter
public class AggregatedStatistics {

    private Long startTimestamp;

    private Long endTimestamp;

    private int totalRequestCount;

    private int totalRequestContentSize;

    private static final int numberOfTopSections = 5;

    private final Map<String, Integer> sectionHitsMap = new HashMap<>();

    private final Map<HTTPMethod, Integer> httpMethodsHitsMap = new HashMap<>();

    /**
     * Increase request count by 1
     */
    public void incrementRequestCount() { this.totalRequestCount++; }

    /**
     * Increase request content size by a given size
     * @param size the size to be added to Aggregated stats
     */
    public void incrementRequestContentSize(int size) { this.totalRequestContentSize += size; }

    public AggregatedStatistics(int totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    /**
     * Overridden toString() to be pushed to console output queue and print to console
     * @return AggregatedStatistics with pretty printing string
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("================ STATISTICS ================\n");

        output.append(String.format("Time Frame: From timestamp  %s to timestamp  %s\n", startTimestamp, endTimestamp));
        output.append(String.format("Total requests served: %s\n", totalRequestCount));
        output.append(String.format("Total content size: %s KB\n", getContentSizeInKB()));

        if (sectionHitsMap.size() > 0) {
            output.append(String.format("Top %s sections by hits:\n", numberOfTopSections));

            getTopSections().forEach((section, hitCount) -> output.append(String.format("\t%s -> %s\n", section, hitCount)));
        }

        if (httpMethodsHitsMap.size() > 0) {
            output.append("HTTP Methods by hits:\n");

            httpMethodsHitsMap.forEach((httpMethod, count) -> output.append(String.format("\t%s -> %s\n", httpMethod.name(), count)));
        }

        output.append("============= End OF STATISTICS =============\n");

        return output.toString();
    }

    private LinkedHashMap<String, Integer> getTopSections() {
        final LinkedHashMap<String, Integer> topSections = new LinkedHashMap<>();
        final NavigableMap<Integer, Set<String>> countVsSectionsMap = new TreeMap<>(Comparator.reverseOrder());

        sectionHitsMap.forEach((k,v) -> {
            countVsSectionsMap.putIfAbsent(v, new HashSet<>());
            countVsSectionsMap.get(v).add(k);
        });

        Set<Integer> topHits = countVsSectionsMap.keySet();
        for (Integer topHit: topHits) {
            for (String section: countVsSectionsMap.get(topHit)) {
                topSections.put(section, topHit);

                if (topSections.size() == numberOfTopSections) {
                    return topSections;
                }
            }
        }

        return topSections;
    }

    private int getContentSizeInKB() {
        return Math.round((float) totalRequestContentSize / 1024);
    }
}
