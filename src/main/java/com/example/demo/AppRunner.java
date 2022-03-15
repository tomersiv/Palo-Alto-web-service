package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    private final Controller controller;

    public AppRunner(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        CompletableFuture<SimilarWords> page1 = controller.similarWords("apple");
        //CompletableFuture<SimilarWords> page2 = controller.similarWords("father");
        //CompletableFuture<SimilarWords> page3 = controller.similarWords("");
        //CompletableFuture<SimilarWords> page4 = controller.similarWords("mother");
        CompletableFuture<Stats> page5 = controller.stats();

        // Wait until they are all done
        CompletableFuture.allOf(page1, page5).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        logger.info("--> " + page1.get());
        //logger.info("--> " + page2.get());
        //logger.info("--> " + page3.get());
        //logger.info("--> " + page4.get());
        logger.info("--> " + page5.get());
    }
}
