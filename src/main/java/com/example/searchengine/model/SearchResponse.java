package com.example.searchengine.model;

import java.util.List;


public class SearchResponse {
    private List<WebDocument> results;
    private int page;
    private int size;
    private int totalResults;
    private int totalPages;
    private long executionTimeMs;

    public SearchResponse(List<WebDocument> results, int page, int size, int totalResults, long executionTimeMs) {
        this.results = results;
        this.page = page;
        this.size = size;
        this.totalResults = totalResults;
        this.totalPages = (int) Math.ceil((double) totalResults / size);
        this.executionTimeMs = executionTimeMs;
    }

    // Getters - these are essential for JSON serialization
    public List<WebDocument> getResults() {
        return results;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
}