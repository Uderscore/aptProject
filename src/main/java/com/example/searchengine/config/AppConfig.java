package com.example.searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import queryProcessor.QueryProcessor;
import queryProcessor.StanfordLemmatizerImpl;
import ranker.rankers.Ranker;

@Configuration
public class AppConfig {

    @Bean
    public StanfordLemmatizerImpl stemmer() {
        return new StanfordLemmatizerImpl();
    }

    @Bean
    public QueryProcessor queryProcessor(StanfordLemmatizerImpl stemmer) {
        return QueryProcessor.getInstance(stemmer);
    }

    @Bean
    public Ranker ranker() {
        return new Ranker();
    }
}