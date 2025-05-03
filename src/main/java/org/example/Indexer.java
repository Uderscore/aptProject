package org.example;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import indexer.IndexManager;
import indexer.utils.MongoConnector;
import org.bson.Document;
import ranker.utils.PageRankAlgo;

import java.util.Arrays;

public class Indexer {

    public static void main(String[] args) {
        MongoConnector.initialize();

        // Number of URLs to index
        int urlLimit = 50;

        // Get random URLs from MongoDB
        MongoCollection<Document> urlsCollection = MongoConnector.getCollection("urls");
        List<String> urls = new ArrayList<>();

        // Use aggregation with $sample to get random documents
        AggregateIterable<Document> randomDocs = urlsCollection.aggregate(
                Arrays.asList(
                        new Document("$sample", new Document("size", urlLimit))
                )
        );

        // Extract URLs from the random documents
        for (Document doc : randomDocs) {
            String url = doc.getString("url");
            if (url != null && !url.isEmpty()) {
                urls.add(url);
            }
        }

        System.out.println("Found " + urls.size() + " random URLs to index");

        // Create index manager with multiple threads
        IndexManager manager = new IndexManager(16); // 4 threads

        // Index each URL asynchronously
        for (String url : urls) {
            manager.indexAsync(url);
        }

        // Wait for all tasks to complete
        try {
            System.out.println("Waiting for indexing to complete...");
            manager.awaitCompletion();
            System.out.println("Indexing completed. Computing PageRank...");
            (new PageRankAlgo(MongoConnector.getCollection("documents"))).computePageRank();
            System.out.println("PageRank computation completed.");
        } catch (InterruptedException e) {
            System.err.println("Indexing interrupted: " + e.getMessage());
        } finally {
            manager.shutdown();
        }
    }
}