package com.example.searchengine.service;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.Properties;

@Component
public class StanfordLemmatizerImpl {
    private StanfordCoreNLP pipeline;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    public StanfordCoreNLP getPipeline() {
        return pipeline;
    }
}