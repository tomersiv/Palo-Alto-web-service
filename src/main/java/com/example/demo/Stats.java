package com.example.demo;

import java.util.List;
//TODO: remove wordsChecked field
public class Stats {
    private int totalWords;
    private int totalRequests;
    private int avgProcessingTimeNs;
    private List<String> wordsChecked;

    public Stats(int totalWords, int totalRequests, int avgProcessingTimeNs){
        this.totalWords = totalWords;
        this.totalRequests = totalRequests;
        this.avgProcessingTimeNs = avgProcessingTimeNs;
        //this.wordsChecked = wordsChecked;
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

    public List<String> getWordsChecked() {
        return wordsChecked;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "totalWords=" + totalWords +
                ", totalRequests=" + totalRequests +
                ", avgProcessingTimeNs=" + avgProcessingTimeNs +
                '}';
    }
}
