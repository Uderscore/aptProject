package com.example.searchengine.controller;


import com.example.searchengine.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/suggestion")
public class SuggestionController {
    private final SuggestionService suggestionService;

    @Autowired
    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public ResponseEntity<List<String>> suggest(
            @RequestParam("prefix") String prefix,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            if (prefix == null || prefix.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(List.of("Error : " + "Prefix cannot be null or empty"));
            }
            List<String> suggestions = suggestionService.getSuggestions(prefix, limit);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of("Server error: " + "An error occurred while fetching suggestions"));
        }
    }
}
