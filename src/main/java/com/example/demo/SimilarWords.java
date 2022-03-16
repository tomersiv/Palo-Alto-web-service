package com.example.demo;

import java.util.Set;

public class SimilarWords {
    private Set<String> similar;

    public SimilarWords(Set<String> similar){
        this.similar = similar;
    }

    public Set<String> getsimilar() {
        return similar;
    }
}
