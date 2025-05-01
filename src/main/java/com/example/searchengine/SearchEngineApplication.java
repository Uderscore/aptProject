package com.example.searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SearchEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApplication.class, args);
    }

    public void hello() {
        System.out.println("Hello, World!");
    }
}