package indexer.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InvertedIndexEntry {
    private String term;
    private int documentFrequency;
    private Map<String, DocumentTermInfo> documents;

    public InvertedIndexEntry(String term) {
        this.term = term;
        this.documentFrequency = 0;
        this.documents = new HashMap<>();
    }

    public void addDocument(String url, double tfTitle, double tfHeadings, double tfBody,
                            List<Integer> titlePositions, List<Integer> headingsPositions, List<Integer> bodyPositions) {
        documents.put(url, new DocumentTermInfo(tfTitle, tfHeadings, tfBody,
                titlePositions, headingsPositions, bodyPositions));
        //documentFrequency++;
    }

    // Getters
    public String getTerm() { return term; }
    public int getDocumentFrequency() { return documentFrequency; }
    public Map<String, DocumentTermInfo> getDocuments() { return documents; }

    public void setDocumentFrequency(int df) { this.documentFrequency = df; }
}

