package com.example.searchengine.controller;

import com.example.searchengine.model.SearchResponse;
import com.example.searchengine.model.WebDocument;
import com.example.searchengine.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private SearchService searchService;

    @Autowired
    public void SearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(value = "topK", defaultValue = "1000") int topK,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            // Start timing
            long startTime = System.currentTimeMillis();

            // Execute search
            List<WebDocument> allResults = searchService.search(query, topK);

            // Apply pagination
            int start = page * size;
            int end = Math.min(start + size, allResults.size());

            List<WebDocument> paginatedResults = start >= allResults.size()
                    ? List.of()
                    : allResults.subList(start, end);

            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;

            // Create response with execution time
            SearchResponse response = new SearchResponse(
                    paginatedResults,
                    page,
                    size,
                    allResults.size(),
                    executionTime
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }
}