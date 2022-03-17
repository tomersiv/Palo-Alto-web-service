package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    private final Controller controller;
    private RestTemplate restTemplate = new RestTemplate();

    public AppRunner(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous tasks
//        CompletableFuture<String> task1 = controller.similarWords("app");
//        CompletableFuture<String> task2 = controller.similarWords("apple");
//        CompletableFuture<String> task3 = controller.similarWords("asdas");
//        CompletableFuture<String> task4 = controller.similarWords("aasdasd");
//        CompletableFuture<String> task5 = controller.stats();
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=apple", String.class));
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=mother", String.class));
        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=father", String.class));
        CompletableFuture<String> task4 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word", String.class));
        //CompletableFuture<String> task5 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/stats", String.class));
        CompletableFuture<String> task6 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=ap", String.class));
        CompletableFuture<String> task7 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=ther", String.class));
        CompletableFuture<String> task8 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=faer", String.class));
        CompletableFuture<String> task9 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=asd", String.class));
        CompletableFuture<String> task10 = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/stats", String.class));
        // Wait until they are all done
        CompletableFuture.allOf(task10).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        logger.info("--> " + task1.get());
        logger.info("--> " + task2.get());
        logger.info("--> " + task3.get());
        logger.info("--> " + task4.get());
        logger.info("--> " + task6.get());
        logger.info("--> " + task7.get());
        logger.info("--> " + task8.get());
        logger.info("--> " + task9.get());
        logger.info("--> " + task10.get());
    }
}
