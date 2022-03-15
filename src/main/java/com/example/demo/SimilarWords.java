package com.example.demo;

import java.util.List;

public class SimilarWords {
    private List<String> similar;

    public SimilarWords(List<String> similar){
        this.similar = similar;
    }

    public List<String> getsimilar() {
        return similar;
    }

    @Override
    public String toString() {
        return "SimilarWords{" +
                "similar=" + similar +
                '}';
    }
}
