package com.example.searchengine.repository;

import com.example.searchengine.model.Term;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TermRepository extends MongoRepository<Term, String> {

    @Query("{ 'term': { $in: ?0 } }")
    List<Term> findAllByTerm(List<String> terms);
}
