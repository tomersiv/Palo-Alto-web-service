package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
@Service
public class Controller {
    private static final int totalWords = 351075;
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicLong totalRequestsTime = new AtomicLong(0);
    private List<String> wordsInFile = readFromFile("words_clean.txt");
    private Set<Integer> lengthSet = new HashSet<>();
    private Map<String, List<String>> wordsMap = calculateMapAndSet(wordsInFile);
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @GetMapping("api/v1/similar")
    @Async
    public CompletableFuture<String> similarWords(@RequestParam(value = "word", defaultValue = "") String word) {
        logger.info("Finding similar words to " + word);
        AtomicLong startTime = new AtomicLong(System.nanoTime());

        if (word.isEmpty() || !lengthSet.contains(word.length())) {
            totalRequests.incrementAndGet();
            AtomicLong duration = new AtomicLong(System.nanoTime() - startTime.get());
            System.out.println("request handle time: " + (duration));
            totalRequestsTime.addAndGet(duration.get());
            return CompletableFuture.completedFuture(objectToJson(new SimilarWords(new HashSet<>())));
        }

        List<String> words = new ArrayList<>();
        Set<Character> wordLetters = new HashSet<>();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if(!wordLetters.contains(ch)) {
                wordLetters.add(ch);
                List<String> wordList = wordsMap.get(String.valueOf(word.length()) + ch);
                if(wordList != null)
                    words.addAll(wordList);
            }
        }

        // check each word in the file to see if it is a permutation of word
        Set<String> simWords = filterSimilarWords(words, word);
        simWords.remove(word);

        SimilarWords similar = new SimilarWords(simWords);
        totalRequests.incrementAndGet();
        AtomicLong duration = new AtomicLong(System.nanoTime() - startTime.get());
        System.out.println("request handle time: " + (duration));
        totalRequestsTime.addAndGet(duration.get());
        return CompletableFuture.completedFuture(objectToJson(similar));
    }

    public Set<String> filterSimilarWords(List<String> words, String word) {
        Set<String> res = new HashSet<>();
        char maxChar = word.charAt(0);
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) - 'a' > maxChar - 'a')
                maxChar = word.charAt(i);
        }
        for (String w : words) {
            if (maxChar < w.charAt(0)) // this line is an optimization to reduce the amount of iterations
                break;
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
    @Async
    public CompletableFuture<String> stats() throws InterruptedException {
        logger.info("Calculating stats...");
        Thread.sleep(2000);
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
