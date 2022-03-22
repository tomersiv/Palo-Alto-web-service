package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {
    private static final int totalWords = 351075;
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicLong totalRequestsTime = new AtomicLong(0);
    private Set<Integer> lengthSet = new HashSet<>();
    private Map<String, List<String>> wordsMap = createMapAndSet("words_clean.txt");

    public Controller() throws IOException {
    }

    @GetMapping("api/v1/similar")
    public String similarWords(@RequestParam(value = "word", defaultValue = "") String word) {
        AtomicLong startTime = new AtomicLong(System.nanoTime());

        /*
         if 'word' is empty or there are no words with the same length as 'word' in the dictionary,
         there are no similar words to 'word'.
        */
        if (word.isEmpty() || !lengthSet.contains(word.length())) {
            totalRequests.incrementAndGet();
            totalRequestsTime.addAndGet(System.nanoTime() - startTime.get());
            return objectToJson(new SimilarWords(new HashSet<>()));
        }

        Set<Character> wordLetters = new HashSet<>();
        Set<String> simWords = new HashSet<>();

        /*
         creates a list of potential similar words to 'word' from the dictionary, for example:
         'word' = "apple" --> 'potential_list' = [list, of, all, words, that, start, with, either, 'a', 'p', 'l' or 'e', and, have, the, same, length, as, "apple"]
        */
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!wordLetters.contains(ch)) {
                wordLetters.add(ch);
                List<String> wordList = wordsMap.get(String.valueOf(word.length()) + ch);
                if (wordList != null)
                    simWords = filterSimilarWords(wordList, word, simWords);
            }
        }
        /*
         remove 'word' if it is presented in the dictionary.
         simWords will now contain all similar words to 'word' not including 'word'.
        */
        simWords.remove(word);

        SimilarWords similar = new SimilarWords(simWords);
        totalRequests.incrementAndGet();
        totalRequestsTime.addAndGet(System.nanoTime() - startTime.get());
        return objectToJson(similar);
    }

    public Set<String> filterSimilarWords(List<String> words, String word, Set<String> similarWords) {
        for (String w : words) {
            if (checkSimilarity(word, w))
                similarWords.add(w);
        }
        return similarWords;
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

    @GetMapping("api/v1/stats")
    public CompletableFuture<String> stats() {
        AtomicInteger avgRequestTime = new AtomicInteger(totalRequests.get() != 0 ? (int) (totalRequestsTime.get() / totalRequests.get()) : 0);
        Stats stats = new Stats(totalWords, totalRequests.get(), avgRequestTime.get());
        return CompletableFuture.completedFuture(objectToJson(stats));
    }
    /*
     creates a HashMap that maps a string to a list of words from the dictionary, in the following way:
     "4a" -> [list, of, words, in, length, 4, that, start, with 'a'].
     also creates a HashSet of all words' lengths in the dictionary.
     */
    public Map<String, List<String>> createMapAndSet(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Map<String, List<String>> map = new HashMap<>();
        String word;
        while ((word = reader.readLine()) != null){
            lengthSet.add(word.length());
            String length = String.valueOf(word.length());
            String letter = String.valueOf(word.charAt(0));
            String code = length + letter;
            if (!map.containsKey(code)) {
                map.put(code, new ArrayList<>());
            } else {
                map.get(code).add(word);
                map.put(code, map.get(code));
            }
        }
        return map;
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

}
