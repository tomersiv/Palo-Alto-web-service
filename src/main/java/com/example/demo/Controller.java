package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Service
public class Controller {
    private static final int totalWords = 351075;
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicLong totalRequestsTime = new AtomicLong(0);
    private List<String> wordsInFile = readFromFile("words_clean.txt");
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @GetMapping("api/v1/similar")
    //@Async
    public String similarWords(@RequestParam(value = "word", defaultValue = "") String word) throws InterruptedException {
        logger.info("Finding similar words to " + word);
        //Thread.sleep(3000);
        AtomicLong startTime = new AtomicLong(System.nanoTime());

        if (word.isEmpty()) {
            totalRequests.incrementAndGet();
            AtomicLong duration = new AtomicLong(System.nanoTime() - startTime.get());
            System.out.println("request handle time: " + (duration));
            totalRequestsTime.addAndGet(duration.get());
            //System.out.println("totalRequestTime in similarWords: " +totalRequestsTime);
            return objectToJson(new SimilarWords(new HashSet<>()));
        }

        // check each word in the file to see if it is a permutation of word
        Set<String> simWords = filterSimilarWords(wordsInFile, word);

//        Set<String> simWords = generatePermutation(new HashSet<>(), word, 0, word.length(), wordsInFile, new HashSet<>());
//        simWords.remove(word);

        SimilarWords similar = new SimilarWords(simWords);
        totalRequests.incrementAndGet();
        AtomicLong duration = new AtomicLong(System.nanoTime() - startTime.get());
        System.out.println("request handle time: " + (duration));
        totalRequestsTime.addAndGet(duration.get());
        //System.out.println("totalRequestTime in similarWords: " +totalRequestsTime);
        return objectToJson(similar);   
    }

    public Set<String> filterSimilarWords(List<String> words, String word) {
        Set<String> res = new HashSet<>();
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

    public boolean checkSimilarity (String word1, String word2){
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

    // checks, for each permutation of 'str', if it is contained in the lexicographically ordered dictionary using binary search
//    public Set<String> generatePermutation(Set<String> res, String str, int start, int end, List<String> words, Set<String> wordsSearched) {
//        if (start == end - 1 && !wordsSearched.contains(str)) {
//            wordsSearched.add(str);
//            int i = Collections.binarySearch(words, str);
//            if (i >= 0)
//                res.add(str);
//        }
//        else {
//            for (int j = start; j < end; j++) {
//                str = swap(str, start, j);
//                generatePermutation(res, str, start + 1, end, words, wordsSearched);
//                str = swap(str, start, j);
//            }
//        }
//        return res;
//    }
//
//    public String swap(String s, int i, int j) {
//        char[] ch = s.toCharArray();
//        char temp = ch[i];
//        ch[i] = ch[j];
//        ch[j] = temp;
//        return String.valueOf(ch);
//    }

        @GetMapping("api/v1/stats")
        //@Async
        public String stats () throws InterruptedException {
            logger.info("Calculating stats...");
            Thread.sleep(1000);
            //System.out.println("totalRequestTime in stats: " + totalRequestsTime);
            AtomicInteger avgRequestTime = new AtomicInteger(totalRequests.get() != 0 ? (int)(totalRequestsTime.get() / totalRequests.get()) : 0);
            Stats stats = new Stats(totalWords, totalRequests.get(), avgRequestTime.get());
            //Thread.sleep(1000L);
            //System.out.println(totalRequestsTime);
            return objectToJson(stats);
        }

        public List<String> readFromFile (String pathName){
            List<String> lst = new ArrayList<>();
            try (Stream<String> lines = Files.lines(Paths.get(pathName))) {
                lst = lines.collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lst;
        }

        public String objectToJson (Object o){
            ObjectMapper Obj = new ObjectMapper();
            try {
                return Obj.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
