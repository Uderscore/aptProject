//package com.example.searchengine.service;
//
//import com.example.searchengine.model.*;
//import com.example.searchengine.repository.DocumentRepository;
//import com.example.searchengine.repository.QueryLogRepository;
//import com.example.searchengine.repository.TermRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
//import static com.example.searchengine.service.utils.Utils.parsePhraseQuery;
//
//
//@Service
//public class SearchService  {
//    private final QueryProcessor queryProcessor;
//    private final Ranker ranker;
//    private final DocumentRepository documentRepository;
//    private final QueryLogRepository queryLogRepository;
//    private final TermRepository termRepository;
//
//    private static final int TITLE_WEIGHT = 20;
//    private static final int HEADING_WEIGHT = 5;
//    private static final int BODY_WEIGHT = 1;
//    private static final int SNIPPET_WINDOW_SIZE = 10; // Words before and after match
//    private static final int MAX_SNIPPET_LENGTH = 200; // Maximum characters in snippet
//
//
//    public SearchService(
//            QueryProcessor queryProcessor,
//            Ranker ranker,
//            DocumentRepository documentRepository,
//            QueryLogRepository queryLogRepository,
////            TermCacheService termCacheService,
//            TermRepository termRepository
//    ) {
//        this.queryProcessor = queryProcessor;
//        this.ranker = ranker;
//        this.documentRepository = documentRepository;
//        this.queryLogRepository = queryLogRepository;
////        this.termCacheService = termCacheService;
//        this.termRepository = termRepository;
//    }
//
//
//    public List<WebDocument> search(String query, int topK) {
//        if (query == null || query.trim().isEmpty()) {
//            throw new IllegalArgumentException("Query cannot be empty");
//        }
//        if (topK <= 0) {
//            throw new IllegalArgumentException("topK must be positive");
//        }
//
//        logQuery(query);
//
//        List<String> terms = queryProcessor.process(query);
//        System.out.println("Terms is" + terms);
//
//        // Fetch all terms at once
//        List<Term> termList = termRepository.findAllByTerm(terms);
//        System.out.println("Term list size is " + termList);
//
//
//
//        Map<String, Term> termCache = termList.stream()
//                .collect(Collectors.toMap(Term::getTerm, t -> t));
//
//
//        System.out.println("Term Cache is" + termCache);
//
//        // Collect all unique URLs
//        Set<String> allUrls = new HashSet<>();
//        for (Term termEntry : termCache.values()) {
//            for (DocumentTermInfo dti : termEntry.getDocuments()) {
//                allUrls.add(dti.getUrl());
//            }
//        }
//
//        System.out.println("all ursl " + allUrls);
//
//        // Bulk query for wordCount
//        List<WebDocument> allDocuments = documentRepository.findByUrlIn(new ArrayList<>(allUrls));
//        System.out.println("All documents size is " + allDocuments);
//        // After bulk query for wordCount
//        Map<String, WebDocument> documentMap = allDocuments.stream()
//                .collect(Collectors.toMap(WebDocument::getUrl, doc -> doc));
//        System.out.println("Document map size is " + documentMap);
//        Map<String, Integer> wordCountMap = allDocuments.stream()
//                .collect(Collectors.toMap(WebDocument::getUrl, WebDocument::getWordCount));
//        System.out.println("Word count map size is " + wordCountMap);
//
//        // Calculate TF-IDF scores
//        Map<String, Double> documentScore = new HashMap<>();
//        long documentCount = documentRepository.count();
//
//        System.out.println("Document Count is " + documentCount);
//
//        for (String term : terms) {
//            Term termEntry = termCache.get(term);
//            if (termEntry == null) continue;
//            int df = termEntry.getDf();
//            if (df == 0) continue;
//            double idf = Math.log((double) documentCount / df);
//            for (DocumentTermInfo dti : termEntry.getDocuments()) {
//                String url = dti.getUrl();
//                if (!documentMap.containsKey(url)) continue;
//                int docLength = wordCountMap.getOrDefault(url, 1);
//                double tfTitle = dti.getTfTitle();
//                double tfHeading = dti.getTfHeadings();
//                double tfBody = dti.getTfBody();
//                double tf = (TITLE_WEIGHT * tfTitle + HEADING_WEIGHT * tfHeading + BODY_WEIGHT * tfBody) / (docLength + 1.0);
//                double tfIdf = tf * idf;
//                documentScore.put(url, documentScore.getOrDefault(url, 0.0) + tfIdf);
//            }
//        }
//
//
//        System.out.println("----------------------------------------");
//        System.out.println("Document Score is " + documentScore);
//        System.out.println("----------------------------------------");
//
//        // Calculate final scores with popularity
//        Map<String, Double> finalScores = ranker.calculatePageRank(documentScore);
//
//        // Get topK URLs
//        List<String> topUrls = finalScores.entrySet().stream()
//                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
//                .limit(topK)
//                .map(Map.Entry::getKey)
//                .toList();
//
//
//
//
//        return topUrls.stream()
//                .map(documentMap::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//    }
//
//
//    /**
//     * Generates a text snippet from the document body based on term positions
//     */
//    private void generateSnippet(WebDocument document, List<String> queryTerms, Map<String, Term> termCache) {
//        if (document.getBody() == null || document.getBody().isEmpty()) {
//            document.setSnippet("");
//            return;
//        }
//
//        String[] bodyWords = document.getBody().split("\\s+");
//        Set<Integer> relevantPositions = new HashSet<>();
//
//        // Collect all positions where query terms appear in the document
//        for (String term : queryTerms) {
//            Term termEntry = termCache.get(term);
//            if (termEntry == null) continue;
//
//            for (DocumentTermInfo dti : termEntry.getDocuments()) {
//                if (dti.getUrl().equals(document.getUrl()) && dti.getBodyPositions() != null) {
//                    relevantPositions.addAll(dti.getBodyPositions());
//                }
//            }
//        }
//
//        if (relevantPositions.isEmpty()) {
//            // If no positions found, use first few words as snippet
//            document.setSnippet(truncateSnippet(document.getBody()));
//            return;
//        }
//
//        // Create snippets around term positions
//        StringBuilder snippetBuilder = new StringBuilder();
//        List<Integer> sortedPositions = new ArrayList<>(relevantPositions);
//        Collections.sort(sortedPositions);
//
//        for (int position : sortedPositions) {
//            if (snippetBuilder.length() >= MAX_SNIPPET_LENGTH) break;
//
//            int start = Math.max(0, position - SNIPPET_WINDOW_SIZE);
//            int end = Math.min(bodyWords.length - 1, position + SNIPPET_WINDOW_SIZE);
//
//            StringBuilder contextSnippet = new StringBuilder();
//
//            // Add "..." at the beginning if not starting from the first word
//            if (start > 0) {
//                contextSnippet.append("... ");
//            }
//
//            // Add words from the context window
//            for (int i = start; i <= end; i++) {
//                contextSnippet.append(bodyWords[i]).append(" ");
//            }
//
//            // Add "..." at the end if not ending at the last word
//            if (end < bodyWords.length - 1) {
//                contextSnippet.append("...");
//            }
//
//            // Add to the main snippet if it's not already included
//            String currentSnippet = contextSnippet.toString();
//            if (!snippetBuilder.toString().contains(currentSnippet)) {
//                if (!snippetBuilder.isEmpty()) {
//                    snippetBuilder.append(" | ");
//                }
//                snippetBuilder.append(currentSnippet);
//            }
//        }
//
//        document.setSnippet(truncateSnippet(snippetBuilder.toString()));
//    }
//
//    private String truncateSnippet(String text) {
//        if (text.length() <= MAX_SNIPPET_LENGTH) {
//            return text;
//        }
//        // Find the last space before MAX_SNIPPET_LENGTH
//        int lastSpace = text.lastIndexOf(' ', MAX_SNIPPET_LENGTH - 3);
//        if (lastSpace == -1) {
//            lastSpace = MAX_SNIPPET_LENGTH - 3;
//        }
//        return text.substring(0, lastSpace) + "...";
//    }
//
//    @Async
//    protected void logQuery(String query) {
//        queryLogRepository.findByQuery(query).ifPresentOrElse(
//                log -> {
//                    log.setCount(log.getCount() + 1);
//                    queryLogRepository.save(log);
//                },
//                () -> {
//                    QueryLog newLog = new QueryLog();
//                    newLog.setQuery(query);
//                    newLog.setCount(1);
//                    queryLogRepository.save(newLog);
//                }
//        );
//    }
//
//
//    private List<WebDocument> performSinglePhraseSearch(String phrase, int topK) {
//        List<String> terms = queryProcessor.process(phrase);
//        if (terms.isEmpty()) {
//            return Collections.emptyList();
//        }
//        String searchQuery = String.join(" ", terms);
////        List<WebDocument> candidates = cachedSearchService.search(searchQuery, topK);
//        List<WebDocument> candidates = search(searchQuery, topK);
//        String lowerPhrase = phrase.toLowerCase();
//
//        //todo kmp
//        return candidates.stream()
//                .filter(doc -> doc.getBody() != null && doc.getBody().toLowerCase().contains(lowerPhrase))
//                .collect(Collectors.toList());
//    }
//
//
//
//    // AND between two phrases
//    private List<WebDocument> performAndPhraseSearch(String phrase1, String phrase2, int topK) {
//        List<String> terms1 = queryProcessor.process(phrase1);
//        List<String> terms2 = queryProcessor.process(phrase2);
//        if (terms1.isEmpty() || terms2.isEmpty()) {
//            return Collections.emptyList();
//        }
//        Set<String> allTerms = new HashSet<>(terms1);
//        allTerms.addAll(terms2);
//        String searchQuery = String.join(" ", allTerms);
////        List<WebDocument> candidates = cachedSearchService.search(searchQuery, topK);
//        List<WebDocument> candidates = search(searchQuery, topK);
//        String lowerPhrase1 = phrase1.toLowerCase();
//        String lowerPhrase2 = phrase2.toLowerCase();
//
//        //todo kmp
//        return candidates.stream()
//                .filter(doc -> doc.getBody() != null &&
//                        doc.getBody().toLowerCase().contains(lowerPhrase1) &&
//                        doc.getBody().toLowerCase().contains(lowerPhrase2))
//                .collect(Collectors.toList());
//    }
//
//
//
//    private List<WebDocument> performOrPhraseSearch(String phrase1, String phrase2, int topK) {
//        List<String> terms1 = queryProcessor.process(phrase1);
//        List<String> terms2 = queryProcessor.process(phrase2);
//        if (terms1.isEmpty() && terms2.isEmpty()) {
//            return Collections.emptyList();
//        }
////        List<WebDocument> candidates1 = terms1.isEmpty() ? Collections.emptyList() : cachedSearchService.search(String.join(" ", terms1), topK);
////        List<WebDocument> candidates2 = terms2.isEmpty() ? Collections.emptyList() : cachedSearchService.search(String.join(" ", terms2), topK);
//        List<WebDocument> candidates1 = terms1.isEmpty() ? Collections.emptyList() : search(String.join(" ", terms1), topK);
//        List<WebDocument> candidates2 = terms2.isEmpty() ? Collections.emptyList() : search(String.join(" ", terms2), topK);
//        Set<WebDocument> allCandidates = new HashSet<>(candidates1);
//        allCandidates.addAll(candidates2);
//        String lowerPhrase1 = phrase1.toLowerCase();
//        String lowerPhrase2 = phrase2.toLowerCase();
//
//        //todo kmp
//        return allCandidates.stream()
//                .filter(doc -> doc.getBody() != null &&
//                        (doc.getBody().toLowerCase().contains(lowerPhrase1) ||
//                                doc.getBody().toLowerCase().contains(lowerPhrase2)))
//                .collect(Collectors.toList());
//    }
//
//    // NOT between two phrases
//    private List<WebDocument> performNotPhraseSearch(String phrase1, String phrase2, int topK) {
//        List<String> terms1 = queryProcessor.process(phrase1);
//        if (terms1.isEmpty()) {
//            return Collections.emptyList();
//        }
////        List<WebDocument> candidates1 = cachedSearchService.search(String.join(" ", terms1), topK);
//        List<WebDocument> candidates1 = search(String.join(" ", terms1), topK);
//        String lowerPhrase1 = phrase1.toLowerCase();
//        String lowerPhrase2 = phrase2.toLowerCase();
//        return candidates1.stream()
//                .filter(doc -> doc.getBody() != null &&
//                        doc.getBody().toLowerCase().contains(lowerPhrase1) &&
//                        !doc.getBody().toLowerCase().contains(lowerPhrase2))
//                .collect(Collectors.toList());
//    }
//
//
//    public List<WebDocument> phraseSearch(String query, int topK) {
//        if (query == null || query.trim().isEmpty()) {
//            throw new IllegalArgumentException("Query cannot be empty");
//        }
//        if (topK <= 0) {
//            throw new IllegalArgumentException("topK must be positive");
//        }
//
//        // Parse the query
//        PhraseQuery parsedQuery = parsePhraseQuery(query);
//
//        // Handle different query types
//        if (parsedQuery.operator() == null) {
//            // Single phrase search
//            return performSinglePhraseSearch(parsedQuery.phrase1(), topK);
//        } else {
//            List<WebDocument> results = switch (parsedQuery.operator().toUpperCase()) {
//                case "AND" -> performAndPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
//                case "OR" -> performOrPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
//                case "NOT" -> performNotPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
//                default -> throw new IllegalArgumentException("Unsupported operator: " + parsedQuery.operator());
//            };
//
//            List<String> terms = queryProcessor.process(query);
//            Map<String, Term> termCache = termRepository.findAllByTerm(terms).stream()
//                    .collect(Collectors.toMap(Term::getTerm, t -> t));
//
////            Map<String, Term> termCache = termCacheService.getCachedTerms(terms).stream()
////                    .collect(Collectors.toMap(Term::getTerm, t -> t));
//
//            for (WebDocument doc : results) {
//                generateSnippet(doc, terms, termCache);
//            }
//
//            return results;
//        }
//    }
//}



package com.example.searchengine.service;

import com.example.searchengine.model.*;
import com.example.searchengine.repository.DocumentRepository;
import com.example.searchengine.repository.QueryLogRepository;
import com.example.searchengine.repository.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.searchengine.service.utils.Utils.parsePhraseQuery;


@Service
public class SearchService {
    private final QueryProcessor queryProcessor;
    private final Ranker ranker;

    private final  ranker.rankers.Ranker oldRanker;

    private final DocumentRepository documentRepository;
    private final TermRepository termRepository;
    private final QueryLogRepository queryLogRepository;

    private static final int TITLE_WEIGHT = 20;
    private static final int HEADING_WEIGHT = 5;
    private static final int BODY_WEIGHT = 1;

    public SearchService(
            QueryProcessor queryProcessor,
            Ranker ranker, DocumentRepository documentRepository,
            TermRepository termRepository,
            QueryLogRepository queryLogRepository,
            ranker.rankers.Ranker oldRanker
    ) {
        this.queryProcessor = queryProcessor;
        this.ranker = ranker;
        this.documentRepository = documentRepository;
        this.termRepository = termRepository;
        this.queryLogRepository = queryLogRepository;
        this.oldRanker = oldRanker;
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

        ///
// Get ordered URLs from the ranker
        List<String> orderedUrls = oldRanker.rank(terms, topK);

        // Fetch documents for these URLs
        List<WebDocument> documents = documentRepository.findByUrlIn(orderedUrls);

        // Create a map for quick lookup
        Map<String, WebDocument> documentMap = documents.stream()
                .collect(Collectors.toMap(WebDocument::getUrl, doc -> doc));

        // Preserve the original ordering from the ranker
        return orderedUrls.stream()
                .map(documentMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());        ///

        // Fetch all terms at once
//        List<Term> termList = termRepository.findAllByTerm(terms);
//
//
//        Map<String, Term> termCache = termList.stream()
//                .collect(Collectors.toMap(Term::getTerm, t -> t));
//
//
//
//        // Collect all unique URLs
//        Set<String> allUrls = new HashSet<>();
//        for (Term termEntry : termCache.values()) {
//            for (DocumentTermInfo dti : termEntry.getDocuments()) {
//                allUrls.add(dti.getUrl());
//            }
//        }


//        List<WebDocument> allDocuments = documentRepository.findByUrlIn(new ArrayList<>(allUrls));
//
//        Map<String, WebDocument> documentMap = allDocuments.stream()
//                .collect(Collectors.toMap(WebDocument::getUrl, doc -> doc));
//        Map<String, Integer> wordCountMap = allDocuments.stream()
//                .collect(Collectors.toMap(WebDocument::getUrl, WebDocument::getWordCount));
//
//
//
//        Map<String, Double> documentScore = new HashMap<>();
//        long documentCount = documentRepository.count();
//
//
//        for (String term : terms) {
//            Term termEntry = termCache.get(term);
//            if (termEntry == null) continue;
//            int df = termEntry.getDf();
//            if (df == 0) continue;
//            double idf = Math.log((double) documentCount / df);
//            for (DocumentTermInfo dti : termEntry.getDocuments()) {
//                String url = dti.getUrl();
//                if (!documentMap.containsKey(url)) continue;
//                int docLength = wordCountMap.getOrDefault(url, 1);
//                double tfTitle = dti.getTfTitle();
//                double tfHeading = dti.getTfHeadings();
//                double tfBody = dti.getTfBody();
//                double tf = (TITLE_WEIGHT * tfTitle + HEADING_WEIGHT * tfHeading + BODY_WEIGHT * tfBody) / (docLength + 1.0);
//                double tfIdf = tf * idf;
//                documentScore.put(url, documentScore.getOrDefault(url, 0.0) + tfIdf);
//            }
//        }
//
//
//
//
//        Map<String, Double> finalScores = ranker.calculatePageRank(documentScore);
//
//
//        List<String> top10Urls = finalScores.entrySet().stream()
//                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
//                .limit(10)
//                .map(Map.Entry::getKey)
//                .toList();
//
//        System.out.println("Top 10 URLs: " + top10Urls);
//
//
//        // Get topK URLs
//        List<String> topUrls = finalScores.entrySet().stream()
//                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
//                .limit(topK)
//                .map(Map.Entry::getKey)
//                .toList();
//
//
//
//
//        return topUrls.stream()
//                .map(documentMap::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());

    }

    @Async
    protected void logQuery(String query) {
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


    // Single phrase search
    private List<WebDocument> performSinglePhraseSearch(String phrase, int topK) {
        List<String> terms = queryProcessor.process(phrase);
        if (terms.isEmpty()) {
            return Collections.emptyList(); // All terms are stop words
        }
        String searchQuery = String.join(" ", terms);
        List<WebDocument> candidates = search(searchQuery, topK);
        String lowerPhrase = phrase.toLowerCase();
        return candidates.stream()
                .filter(doc -> doc.getBody() != null && doc.getBody().toLowerCase().contains(lowerPhrase))
                .collect(Collectors.toList());
    }



    // AND between two phrases
    private List<WebDocument> performAndPhraseSearch(String phrase1, String phrase2, int topK) {
        List<String> terms1 = queryProcessor.process(phrase1);
        List<String> terms2 = queryProcessor.process(phrase2);
        if (terms1.isEmpty() || terms2.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> allTerms = new HashSet<>(terms1);
        allTerms.addAll(terms2);
        String searchQuery = String.join(" ", allTerms);
        List<WebDocument> candidates = search(searchQuery, topK);
        String lowerPhrase1 = phrase1.toLowerCase();
        String lowerPhrase2 = phrase2.toLowerCase();

        //todo kmp
        return candidates.stream()
                .filter(doc -> doc.getBody() != null &&
                        doc.getBody().toLowerCase().contains(lowerPhrase1) &&
                        doc.getBody().toLowerCase().contains(lowerPhrase2))
                .collect(Collectors.toList());
    }



    private List<WebDocument> performOrPhraseSearch(String phrase1, String phrase2, int topK) {
        List<String> terms1 = queryProcessor.process(phrase1);
        List<String> terms2 = queryProcessor.process(phrase2);
        if (terms1.isEmpty() && terms2.isEmpty()) {
            return Collections.emptyList();
        }
        List<WebDocument> candidates1 = terms1.isEmpty() ? Collections.emptyList() : search(String.join(" ", terms1), topK);
        List<WebDocument> candidates2 = terms2.isEmpty() ? Collections.emptyList() : search(String.join(" ", terms2), topK);
        Set<WebDocument> allCandidates = new HashSet<>(candidates1);
        allCandidates.addAll(candidates2);
        String lowerPhrase1 = phrase1.toLowerCase();
        String lowerPhrase2 = phrase2.toLowerCase();

        return allCandidates.stream()
                .filter(doc -> doc.getBody() != null &&
                        (doc.getBody().toLowerCase().contains(lowerPhrase1) ||
                                doc.getBody().toLowerCase().contains(lowerPhrase2)))
                .collect(Collectors.toList());
    }

    // NOT between two phrases
    private List<WebDocument> performNotPhraseSearch(String phrase1, String phrase2, int topK) {
        List<String> terms1 = queryProcessor.process(phrase1);
        if (terms1.isEmpty()) {
            return Collections.emptyList();
        }

        List<WebDocument> candidates1 = search(String.join(" ", terms1), topK);
        String lowerPhrase1 = phrase1.toLowerCase();
        String lowerPhrase2 = phrase2.toLowerCase();
        return candidates1.stream()
                .filter(doc -> doc.getBody() != null &&
                        doc.getBody().toLowerCase().contains(lowerPhrase1) &&
                        !doc.getBody().toLowerCase().contains(lowerPhrase2))
                .collect(Collectors.toList());
    }


    public List<WebDocument> phraseSearch(String query, int topK) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }

        // Parse the query
        PhraseQuery parsedQuery = parsePhraseQuery(query);

        // Handle different query types
        if (parsedQuery.operator() == null) {
            // Single phrase search
            return performSinglePhraseSearch(parsedQuery.phrase1(), topK);
        } else {
            List<WebDocument> results = switch (parsedQuery.operator().toUpperCase()) {
                case "AND" -> performAndPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
                case "OR" -> performOrPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
                case "NOT" -> performNotPhraseSearch(parsedQuery.phrase1(), parsedQuery.phrase2(), topK);
                default -> throw new IllegalArgumentException("Unsupported operator: " + parsedQuery.operator());
            };

            List<String> terms = queryProcessor.process(query);
            Map<String, Term> termCache = termRepository.findAllByTerm(terms).stream()
                    .collect(Collectors.toMap(Term::getTerm, t -> t));

            return results;
        }
    }
}