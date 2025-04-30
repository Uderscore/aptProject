package indexer.models;

import java.util.HashMap;
import java.util.Map;

public class InvertedIndexEntry {
    private String term;
    private int documentFrequency;
    private Map<String, DocumentTermInfo> documents;

    public InvertedIndexEntry(String term) {
        this.term = term;
        this.documentFrequency = 0;
        this.documents = new HashMap<>();
    }

    public void addDocument(String url, double tfTitle, double tfHeadings, double tfBody) {
        documents.put(url, new DocumentTermInfo(tfTitle, tfHeadings, tfBody));
        documentFrequency++;
    }

    // Getters
    public String getTerm() { return term; }
    public int getDocumentFrequency() { return documentFrequency; }
    public Map<String, DocumentTermInfo> getDocuments() { return documents; }

    public void setDocumentFrequency(int df) { this.documentFrequency = df; }
}

