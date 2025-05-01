package com.example.searchengine.repository;

import com.example.searchengine.model.WebDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentRepository extends MongoRepository<WebDocument, String> {
    List<WebDocument> findByUrlIn(List<String> urls);
}