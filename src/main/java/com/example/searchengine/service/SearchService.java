package com.example.searchengine.service;

import com.example.searchengine.model.QueryLog;
import com.example.searchengine.model.WebDocument;
import com.example.searchengine.repository.DocumentRepository;
import com.example.searchengine.repository.QueryLogRepository;
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
    private final QueryLogRepository queryLogRepository;

    @Autowired
    public SearchService(
            QueryProcessor queryProcessor,
            Ranker ranker,
            DocumentRepository documentRepository,
            QueryLogRepository queryLogRepository
            ) {
        this.queryProcessor = queryProcessor;
        this.ranker = ranker;
        this.documentRepository = documentRepository;
        this.queryLogRepository = queryLogRepository;
    }

    public List<WebDocument> search(String query, int topK) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }

        logQuery(query);
        List<String> terms = queryProcessor.process(query);
        List<String> urls = ranker.rank(terms, topK);

        // Fetch document details from MongoDB
        return documentRepository.findByUrlIn(urls);
    }

    private void logQuery(String query) {
        queryLogRepository.findByQuery(query).ifPresentOrElse(
                log -> {
                    log.setCount(log.getCount() + 1);
                    queryLogRepository.save(log);
                },
                () -> {
                    QueryLog newLog = new QueryLog();
                    newLog.setQuery(query);
                    newLog.setCount(1);
                    queryLogRepository.save(newLog);
                }
        );
    }
}