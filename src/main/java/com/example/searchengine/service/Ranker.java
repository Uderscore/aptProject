package com.example.searchengine.service;

import com.example.searchengine.model.WebDocument;
import com.example.searchengine.repository.DocumentRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Ranker {
    private final DocumentRepository documentRepository;

    public Ranker(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Map<String, Double> calculatePageRank(Map<String, Double> documentScore) {
        if (documentScore.isEmpty()) {
            return documentScore;
        }

        // Fetch all popularity scores in a single query
        List<String> urls = documentScore.keySet().stream().toList();
        List<WebDocument> documents = documentRepository.findPopularityByUrlIn(urls);

        // Create a map of URL -> popularity score for faster lookups
        Map<String, Double> popularityMap = documents.stream()
                .collect(Collectors.toMap(
                        WebDocument::getUrl,
                        WebDocument::getPopularity,
                        (existing, replacement) -> existing));

        //get the top 10 docums on score
        List<String> top10Urls = documentScore.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("Top 10 URLs: " + top10Urls);

        // Calculate final scores using the popularity map
        return documentScore.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Double popularity = popularityMap.getOrDefault(entry.getKey(), 0.0);
                            return entry.getValue() * 0.80 + popularity * 0.20;
                        }
                ));
    }
}