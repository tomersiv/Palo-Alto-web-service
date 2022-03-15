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

        // Kick of multiple, asynchronous tasks
        CompletableFuture<SimilarWords> task1 = controller.similarWords("apple");
        CompletableFuture<SimilarWords> task2 = controller.similarWords("father");
        CompletableFuture<SimilarWords> task3 = controller.similarWords("");
        CompletableFuture<SimilarWords> task4 = controller.similarWords("mother");
        CompletableFuture<Stats> task5 = controller.stats();

        // Wait until they are all done
        CompletableFuture.allOf(task1, task2, task3, task4, task5).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        logger.info("--> " + task1.get());
        logger.info("--> " + task2.get());
        logger.info("--> " + task3.get());
        logger.info("--> " + task4.get());
        logger.info("--> " + task5.get());
    }
}
