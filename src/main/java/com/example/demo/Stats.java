package com.example.demo;

public class Stats {
    private int totalWords;
    private int totalRequests;
    private int avgProcessingTimeNs;

    public Stats(int totalWords, int totalRequests, int avgProcessingTimeNs){
        this.totalWords = totalWords;
        this.totalRequests = totalRequests;
        this.avgProcessingTimeNs = avgProcessingTimeNs;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getAvgProcessingTimeNs() {
        return avgProcessingTimeNs;
    }
}
