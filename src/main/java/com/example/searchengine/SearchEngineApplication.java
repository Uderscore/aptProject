package com.example.searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
//@EnableCaching()
//@EnableAsync()
public class SearchEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApplication.class, args);
    }

}