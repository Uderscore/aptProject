package com.example.searchengine.service.utils;
import com.example.searchengine.model.PhraseQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    // Parse the query to extract phrases and operator
    public static PhraseQuery parsePhraseQuery(String query) {
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*(AND|OR|NOT)?\\s*(\"([^\"]+)\")?");
        Matcher matcher = pattern.matcher(query.trim());

        if (matcher.find()) {
            String phrase1 = matcher.group(1);
            String operator = matcher.group(2);
            String phrase2 = matcher.group(4);
            if (operator != null && phrase2 == null) {
                throw new IllegalArgumentException("Second phrase required for operator: " + operator);
            }
            return new PhraseQuery(phrase1, phrase2, operator);
        } else {
            throw new IllegalArgumentException("Invalid phrase search query format");
        }
    }
}