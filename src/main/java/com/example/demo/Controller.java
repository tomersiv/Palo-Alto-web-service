package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class Controller {
    private static final int totalWords = 351075;
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicLong totalRequestsTime = new AtomicLong(0);
    private List<String> wordsInFile = readFromFile("words_clean.txt");
    private Set<Integer> lengthSet = new HashSet<>();
    private Map<String, List<String>> wordsMap = calculateMapAndSet(wordsInFile);

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

        List<String> words = new ArrayList<>();
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
                    words.addAll(wordList);
            }
        }

        // simWords will contain all similar words to 'word'
        if (!words.isEmpty()) {
            simWords = filterSimilarWords(words, word);
            simWords.remove(word);
        }

        SimilarWords similar = new SimilarWords(simWords);
        totalRequests.incrementAndGet();
        totalRequestsTime.addAndGet(System.nanoTime() - startTime.get());
        return objectToJson(similar);
    }

    public Set<String> filterSimilarWords(List<String> words, String word) {
        Set<String> res = new HashSet<>();
        for (String w : words) {
            if (checkSimilarity(word, w))
                res.add(w);
        }
        return res;
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

    public List<String> readFromFile(String pathName) {
        List<String> lst = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(pathName))) {
            lst = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lst;
    }

    /*
     creates a HashMap that maps a string to a list of words from the dictionary, for Example:
     "4a" -> [list, of, words, in, length, 4, that, start, with 'a'].
     also creates a HashSet of all words' lengths in the dictionary.
     */
    public Map<String, List<String>> calculateMapAndSet(List<String> wordsInFile) {
        Map<String, List<String>> map = new HashMap<>();
        for (String w : wordsInFile) {
            lengthSet.add(w.length());
            String length = String.valueOf(w.length());
            String letter = String.valueOf(w.charAt(0));
            String code = length + letter;
            if (!map.containsKey(code)) {
                map.put(code, new ArrayList<>());
            } else {
                map.get(code).add(w);
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
