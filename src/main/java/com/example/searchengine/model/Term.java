package com.example.searchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Document(collection = "terms")
public class Term implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id; // Unique identifier for the term
    @Indexed
    private String term;
    private int df;
    private List<DocumentTermInfo> documents;

    // Getters and setters
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public int getDf() { return df; }
    public void setDf(int df) { this.df = df; }
    public List<DocumentTermInfo> getDocuments() { return documents; }
    public void setDocuments(List<DocumentTermInfo> documents) { this.documents = documents; }

    @Override
    public String toString() {
        return "Term{" +
                "id='" + id + '\'' +
                ", term='" + term + '\'' +
                ", df=" + df +
                ", documents=" + documents +
                '}';
    }
}