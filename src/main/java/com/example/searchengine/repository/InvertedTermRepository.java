package com.example.searchengine.repository;

import com.example.searchengine.model.InvertedTerm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvertedTermRepository extends MongoRepository<InvertedTerm, String> {
    Optional<InvertedTerm> findByTerm(String term);

}
