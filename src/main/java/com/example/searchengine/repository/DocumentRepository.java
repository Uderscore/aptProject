package com.example.searchengine.repository;

import com.example.searchengine.model.WebDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface DocumentRepository extends MongoRepository<WebDocument, String> {
    List<WebDocument> findByUrlIn(List<String> urls);
    @Query(value = "{ 'url': { $in: ?0 } }", fields = "{ 'url': 1,  'wordCount': 1 }")
    List<WebDocument> findByUrlInProjected(List<String> urls);
    @Query(value = "{ 'url': { $in: ?0 } }", fields = "{ 'url': 1, 'title': 1, 'snippet': 1, '_id': 1 }")
    List<WebDocument> findByUrlInForSearch(List<String> urls);

    @Query(value = "{ 'url': { $in: ?0 } }", fields = "{ 'url': 1, 'popularity': 1 }")
    List<WebDocument> findPopularityByUrlIn(List<String> urls);
}