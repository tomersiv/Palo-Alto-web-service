package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class Controller {
    private static final int totalWords = 351075;
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger totalRequestsTime = new AtomicInteger(0);
    private List<String> wordsInFile = readFromFile("words_clean.txt");

    @GetMapping("api/v1/similar")
    public String similarWords(@RequestParam(value = "word", defaultValue = "") String word) {
        AtomicLong startTime = new AtomicLong(System.nanoTime());

        if (word.isEmpty()) {
            totalRequests.incrementAndGet();
            totalRequestsTime.addAndGet((int) (System.nanoTime() - startTime.get()));
            return objectToJson(new SimilarWords(new ArrayList<>()));
        }

        // check each word in the file to see if it is a permutation of word
        List<String> simWords = filterSimilarWords(wordsInFile, word);

        SimilarWords similar = new SimilarWords(simWords);
        totalRequests.incrementAndGet();
        totalRequestsTime.addAndGet((int) (System.nanoTime() - startTime.get()));
        return objectToJson(similar);
    }

    public List<String> filterSimilarWords(List<String> words, String word) { //TODO: maybe add a hashmap optimization
        List<String> res = new ArrayList<>();
        Set<Character> s = new HashSet<>();
        char maxChar = word.charAt(0);
        for (int i = 0; i < word.length(); i++) {
            s.add(word.charAt(i));
            if (word.charAt(i) - 'a' > maxChar - 'a')
                maxChar = word.charAt(i);
        }
        for (String w : words) {
            if (maxChar < w.charAt(0)) // this line is an optimization to reduce the amount of iterations
                break;
            if (word.length() == w.length() && s.contains(w.charAt(0))  // this line is also an optimization
                    && !word.equals(w) && checkSimilarity(word, w))
                res.add(w);
        }
        return res;
    }

    @GetMapping("api/v1/stats")
    public String stats() {
        AtomicInteger avgRequestTime = new AtomicInteger(totalRequests.get() != 0 ? (totalRequestsTime.get() / totalRequests.get()) : 0);
        Stats stats = new Stats(totalWords, totalRequests.get(), avgRequestTime.get());
        return objectToJson(stats);
    }

    public List<String> readFromFile(String pathName) {
        List<String> lst = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(pathName))) {
            lst = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lst;
    }

    public String objectToJson(Object o) {
        ObjectMapper Obj = new ObjectMapper();
        try {
            return Obj.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkSimilarity(String word1, String word2) {
        int[] count = new int[26];
        int i;
        for (i = 0; i < word1.length(); i++) {
            count[word1.charAt(i) - 'a']++;
            count[word2.charAt(i) - 'a']--;
        }
        for (i = 0; i < 26; i++)
            if (count[i] != 0) {
                return false;
            }
        return true;
    }

}
