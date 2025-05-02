package com.example.searchengine.repository;

import com.example.searchengine.model.QueryLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QueryLogRepository extends MongoRepository<QueryLog, String> {

    /**
     * helps to check if the query is already present in the database
     * @param query the query string to search for
     * @return  an Optional containing the QueryLog if found, or an empty Optional if not found
     */
    Optional<QueryLog> findByQuery(String query);
}
