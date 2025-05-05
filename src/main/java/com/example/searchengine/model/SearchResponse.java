package com.example.searchengine.model;

import java.util.List;
import java.util.stream.Collectors;

//
//public record SearchResponse(
//        List<WebDocumentDTO> results,
//        int page,
//        int size,
//        int totalResults,
//        int totalPages,
//        long executionTimeMs
//) {
//
//
//    // Additional constructor for WebDocument conversion
//    public SearchResponse {
//        // Record compact constructor - validation can go here
//        if (results == null) {
//            throw new IllegalArgumentException("Results cannot be null");
//        }
//    }
//
//    // Static factory method for conversion from WebDocument
//    public static SearchResponse fromWebDocuments(
//            List<WebDocument> documents,
//            int page,
//            int size,
//            int totalResults,
//            int totalPages,
//            long executionTimeMs
//    ) {
//        return new SearchResponse(
//                documents.stream()
//                        .map(WebDocumentDTO::fromEntity)
//                        .collect(Collectors.toList()),
//                page,
//                size,
//                totalResults,
//                totalPages,
//                executionTimeMs
//        );
//    }
//}


//package com.example.searchengine.model;
//
//import java.util.List;
//

public class SearchResponse {
    private List<WebDocument> results;
    private int page;
    private int size;
    private int totalResults;
    private int totalPages;
    private long executionTimeMs;

    public SearchResponse(List<WebDocument> results, int page, int size, int totalResults, int totalPages, long executionTimeMs) {
        this.results = results;
        this.page = page;
        this.size = size;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.executionTimeMs = executionTimeMs;
    }

    // Getters
    public List<WebDocument> getResults() { return results; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalResults() { return totalResults; }
    public int getTotalPages() { return totalPages; }
    public long getExecutionTimeMs() { return executionTimeMs; }
}