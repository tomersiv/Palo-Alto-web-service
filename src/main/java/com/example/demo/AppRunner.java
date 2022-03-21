package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
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
        CompletableFuture<String>[] tasks = new CompletableFuture[100];
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous tasks
//        CompletableFuture<String> task1 = controller.similarWords("app");
//        logger.info(task1.get());
//        CompletableFuture<String> task2 = controller.similarWords("apple");
//        CompletableFuture<String> task3 = controller.similarWords("asdas");
//        CompletableFuture<String> task4 = controller.similarWords("aasdasd");
//        CompletableFuture<String> task5 = controller.similarWords("aasdasdasdae");
//        CompletableFuture<String> task6 = controller.similarWords("aasdasdasdasdqwe");
//        CompletableFuture<String> task7 = controller.stats();
//        logger.info(task7.get());

        for(int i = 0; i < 99; i++){
            String str = generateString();
            tasks[i] = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/similar?word=" + str, String.class));
        }
        tasks[99] = CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://localhost:8000/api/v1/stats", String.class));

        // Wait until they are all done
        CompletableFuture.allOf(tasks).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        for(int i = 0; i < 100; i++) {
            logger.info("--> " + tasks[i].get());
        }
    }

    public String generateString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = new Random().nextInt(30);
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
