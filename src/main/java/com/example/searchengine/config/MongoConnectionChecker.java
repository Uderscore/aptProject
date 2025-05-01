package com.example.searchengine.config;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoConnectionChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoConnectionChecker.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        try {
            // Check connection
            Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            logger.info("Successfully connected to MongoDB!");

            // Count documents
            long count = mongoTemplate.getCollection("documents").countDocuments();
            logger.info("Found {} documents in the 'documents' collection", count);

            // List all collections
            logger.info("Available collections: {}", mongoTemplate.getCollectionNames());

        } catch (Exception e) {
            logger.error("Failed to connect to MongoDB: {}", e.getMessage(), e);
        }
    }
}