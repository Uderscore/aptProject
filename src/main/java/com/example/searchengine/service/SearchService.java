package com.example.searchengine.service;

import com.example.searchengine.model.WebDocument;
import com.example.searchengine.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ranker.rankers.Ranker;
import queryProcessor.QueryProcessor;

import java.util.List;

@Service
public class SearchService {

    private final QueryProcessor queryProcessor;
    private final Ranker ranker;
    private final DocumentRepository documentRepository;

    @Autowired
    public SearchService(QueryProcessor queryProcessor, Ranker ranker, DocumentRepository documentRepository) {
        this.queryProcessor = queryProcessor;
        this.ranker = ranker;
        this.documentRepository = documentRepository;
    }

    public List<WebDocument> search(String query, int topK) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }

        List<String> terms = queryProcessor.process(query);
        List<String> urls = ranker.rank(terms, topK);

        // Fetch document details from MongoDB
        return documentRepository.findByUrlIn(urls);
    }
}