package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class SimilarWordsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimilarWordsApplication.class, args).close();
	}
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(15);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("SimilarWordsThread-");
		executor.initialize();
		return executor;
	}

}
