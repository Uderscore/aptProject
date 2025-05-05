package com.example.searchengine.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QueryProcessor {
    private final StanfordLemmatizerImpl lemmatizer;
    private final Set<String> stopWords;

    public QueryProcessor(StanfordLemmatizerImpl lemmatizer) {
        this.stopWords = loadStopWords();
        this.lemmatizer = lemmatizer;
    }

    public List<String> process(String query) {
        String cleanedQuery = query.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        Annotation document = new Annotation(cleanedQuery);
        lemmatizer.getPipeline().annotate(document);

        List<String> processedQuery = new ArrayList<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String original = token.get(CoreAnnotations.TextAnnotation.class);
                if (!stopWords.contains(original)) {
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    processedQuery.add(lemma);
                }
            }
        }
        return processedQuery;
    }

    private Set<String> loadStopWords() {
        Set<String> stopWords = new HashSet<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("stop_words.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stopWords.add(line.trim().toLowerCase());
                    }
                }
            } else {
                System.err.println("Stop words file not found in resources");
                // Fallback to default stop words
                Collections.addAll(stopWords,
                        "a", "an", "the", "this", "that", "is", "are", "was", "were", "be", "been");
            }
        } catch (IOException e) {
            System.err.println("Error loading stop words: " + e.getMessage());
            // Fallback to default stop words
            Collections.addAll(stopWords,
                    "a", "an", "the", "this", "that", "is", "are", "was", "were", "be", "been");
        }
        return stopWords;
    }
}