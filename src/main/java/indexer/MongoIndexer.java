package indexer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import indexer.models.Document;
import indexer.models.InvertedIndexEntry;
import indexer.models.Term;
import indexer.utils.MongoConnector;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class MongoIndexer {
    private final MongoCollection<org.bson.Document> documentsCollection;
    private final MongoCollection<org.bson.Document> termsCollection;
    private final Tokenizer tokenizer;

    public MongoIndexer() {
        this.documentsCollection = MongoConnector.getCollection("documents");
        this.termsCollection = MongoConnector.getCollection("terms");
        this.tokenizer = new Tokenizer();
    }

    public boolean isUrlIndexed(String url) {
        return documentsCollection.countDocuments(eq("url", url)) > 0;
    }

    public void indexDocument(Document doc) {
        // Check if the document is already indexed
        if (isUrlIndexed(doc.getUrl())) {
            System.out.println("Document already indexed: " + doc.getUrl());
            return;
        }

        // First, store the document metadata
        org.bson.Document documentDoc = new org.bson.Document()
                .append("url", doc.getUrl())
                .append("title", doc.getTitle())
                .append("headings", doc.getHeadings())
                .append("body", doc.getBody());

        documentsCollection.insertOne(documentDoc);

        // Process text content for indexing
        Map<String, Term> terms = new HashMap<>();

        // Process title (higher weight)
        processText(doc.getTitle(), terms, 2.0);
        System.out.println("Processing title done");

        // Process headings (medium weight)
        processText(doc.getHeadings(), terms, 1.5);
        System.out.println("Processing headings done");

        // Process body (normal weight)
        processText(doc.getBody(), terms, 1.0);
        System.out.println("Processing body done");

        // Update inverted index in MongoDB
        // I know maybe it seems a bit complicated, so If you want to understand look at the data structure & our DB
        for (Map.Entry<String, Term> entry : terms.entrySet()) {
            String term = entry.getKey();
            Term termData = entry.getValue();

            Bson filter = eq("term", term);
            Bson update = combine(
                    set("term", term),
                    push("documents",
                            new org.bson.Document()
                                    .append("url", doc.getUrl())
                                    .append("tf_title", termData.getTfTitle())
                                    .append("tf_headings", termData.getTfHeadings())
                                    .append("tf_body", termData.getTfBody())
                    ),
                    inc("df", 1)
            );

            System.out.println("Updating term document: " + term);

            UpdateOptions options = new UpdateOptions().upsert(true);
            termsCollection.updateOne(filter, update, options);

            System.out.println("Updating done");
        }

        System.out.println("Document indexed: " + doc.getUrl());
    }

    private void processText(String text, Map<String, Term> terms, double weight) {
        List<String> tokens = tokenizer.tokenize(text);

        for (String token : tokens) {
            Term term = terms.getOrDefault(token, new Term(token));

            if (weight == 2.0) {
                term.incrementTfTitle();
            } else if (weight == 1.5) {
                term.incrementTfHeadings();
            } else {
                term.incrementTfBody();
            }

            terms.put(token, term);
        }
    }

    /*
     * This method retrieves the inverted index entry for a given term.
     * It returns an InvertedIndexEntry object containing the term and its associated documents.
     * I think this will be useful for Kamal
     * Note: I will not use it in the current version of the code, but I will keep it for future use (the actual search)
     */
    public InvertedIndexEntry getDocumentsForTerm(String term) {
        org.bson.Document termDoc = termsCollection.find(eq("term", term)).first();
        if (termDoc == null) {
            return null;
        }

        InvertedIndexEntry entry = new InvertedIndexEntry(term);
        entry.setDocumentFrequency(termDoc.getInteger("df"));

        List<org.bson.Document> docEntries = termDoc.getList("documents", org.bson.Document.class);
        for (org.bson.Document docEntry : docEntries) {
            entry.addDocument(
                    docEntry.getString("url"),
                    docEntry.getDouble("tf_title"),
                    docEntry.getDouble("tf_headings"),
                    docEntry.getDouble("tf_body")
            );
        }

        return entry;
    }
}