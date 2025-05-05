package com.example.searchengine.controller;

import com.example.searchengine.model.SearchResponse;
import com.example.searchengine.model.WebDocument;
import com.example.searchengine.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(value = "topK", defaultValue = "100") int topK,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            // Start timing
            long startTime = System.currentTimeMillis();

            // Execute search
            List<WebDocument> allResults = searchService.search(query, topK);


            return getResponseEntity(page, size, startTime, allResults);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/phrase")
    public ResponseEntity<?> phraseSearch(
            @RequestParam("query") String query,
            @RequestParam(value = "topK", defaultValue = "100") int topK,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            long startTime = System.currentTimeMillis();
            List<WebDocument> allResults = searchService.phraseSearch(query, topK);
            return getResponseEntity(page, size, startTime, allResults);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private ResponseEntity<?> getResponseEntity
            (
            @RequestParam(value = "page", defaultValue = "0")
            int page,
            @RequestParam(value = "size", defaultValue = "10")
            int size,
            long startTime,
            List<WebDocument> allResults
    ) {
        int start = page * size;
        int end = Math.min(start + size, allResults.size());
        List<WebDocument> paginatedResults = (start >= allResults.size()) ? List.of() : allResults.subList(start, end);
        long executionTime = System.currentTimeMillis() - startTime;
        int totalResults = allResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / size);
        SearchResponse response = new SearchResponse(
                paginatedResults,
                page,
                size,
                totalResults,
                totalPages,
                executionTime
        );
        return ResponseEntity.ok(response);
    }
}









//
//import com.example.searchengine.model.SearchResponse;
//import com.example.searchengine.model.WebDocument;
//import com.example.searchengine.service.SearchService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.List;
//import java.util.Map;
//
//
//@RestController
//@RequestMapping("api/v1/search")
//public class SearchController {
//    private final SearchService searchService;
//
//    public SearchController(SearchService searchService) {
//        this.searchService = searchService;
//    }
//
//    @GetMapping
//    public ResponseEntity<?> search(
//            @RequestParam("query") String query,
//            @RequestParam(value = "topK", defaultValue = "100") int topK,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "10") int size) {
//        try {
//            // Start timing
//            long startTime = System.currentTimeMillis();
//
//            // Execute search
//            System.out.println("Query is " + query);
//            List<WebDocument> allResults = searchService.search(query, topK);
//            System.out.println("All results size is " + allResults);
//
//
//            // Apply pagination
//            int start = page * size;
//            int end = Math.min(start + size, allResults.size());
//            List<WebDocument> paginatedResults = (start >= allResults.size()) ? List.of() : allResults.subList(start, end);
//
//            // Calculate execution time
//            long executionTime = System.currentTimeMillis() - startTime;
//
//            // Calculate total pages
//            int totalResults = allResults.size();
//            int totalPages = (int) Math.ceil((double) totalResults / size);
//
//            // Create response
//            SearchResponse response = new SearchResponse(paginatedResults, page, size, totalResults, totalPages, executionTime);
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//            Map<String, String> errorResponse = Map.of("error", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = Map.of("error", "An unexpected error occurred: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(errorResponse);
//        }
//    }
//}
