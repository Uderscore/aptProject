package org.example;

import java.util.ArrayList;
import java.util.List;

import indexer.IndexManager;
import indexer.utils.MongoConnector;

public class Indexer {

    public static void main(String[] args) {
        MongoConnector.initialize();
        IndexManager manager = new IndexManager(4); // 4 threads

        // will be changed later
        List<String> urls = new ArrayList<>();
        urls = List.of(
                "https://github.com/MarioRaafat"
        );

        for (String url : urls) {
            manager.indexAsync(url);
        }

        // Wait for all tasks to complete
        try {
            manager.awaitCompletion();
        } catch (InterruptedException e) {
            System.err.println("Indexing interrupted: " + e.getMessage());
        } finally {
            manager.shutdown();
        }
    }
}