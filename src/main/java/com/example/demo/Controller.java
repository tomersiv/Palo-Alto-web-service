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
public class Controller {a
    private AtomicInteger totalWords = new AtomicInteger(0);
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger totalRequestsTime = new AtomicInteger(0);

    @GetMapping("api/v1/similar")
    public String similarWords(@RequestParam(value = "word", defaultValue = "") String word) {
        AtomicLong startTime = new AtomicLong(System.nanoTime());
        List<String> wordsInFile = new ArrayList<>();
        List<String> simWords;
        wordsInFile = readFromFile("words_clean.txt", wordsInFile);
        totalWords.set(wordsInFile.size());
        totalRequests.incrementAndGet();

        // check each word in the file to see if it is a permutation of word
        simWords = filterSimilarWords(wordsInFile, word);

        SimilarWords similar = new SimilarWords(simWords);
        AtomicLong endTime = new AtomicLong(System.nanoTime());
        totalRequestsTime.addAndGet((int)(endTime.get() - startTime.get()));
        return objectToJson(similar);
    }

    public List<String> filterSimilarWords(List<String> words, String word) {
        List<String> res = new ArrayList<>();
        Set<Character> s= new HashSet<>();
        for(int i = 0; i < word.length(); i++)
            s.add(word.charAt(i));
        for(String w : words) {
            if(word.length() == w.length() && s.contains(w.charAt(0)) && !word.equals(w) && checkSimilarity(word, w))
                res.add(w);
        }
        return res;
    }

    @GetMapping("api/v1/stats")
    public String stats(){
            //get total words num
            try {
                Stream<String> lines = Files.lines(Paths.get("words_clean.txt"));
                totalWords.set((int)lines.count());
            } catch (IOException e) {
                e.printStackTrace();
            }
        int avgRequestTime = totalRequests.get() != 0 ? (totalRequestsTime.get() / totalRequests.get()) : totalRequestsTime.get();
        Stats stats = new Stats(totalWords.get(), totalRequests.get(), avgRequestTime);
        return objectToJson(stats);
    }

    public List<String> readFromFile(String pathName, List<String> lst){
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
            String jsonStr = Obj.writeValueAsString(o);
            return jsonStr;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkSimilarity(String word1, String word2) {
        if (word1.length() != word2.length())
            return false;
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
