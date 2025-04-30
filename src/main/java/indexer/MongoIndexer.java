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
                .append("body", doc.getBody())
                .append("outgoingLinks", doc.getOutgoingLinks());

        documentsCollection.insertOne(documentDoc);

        // Process text content for indexing
        Map<String, Term> terms = new HashMap<>();

        // Process title (higher weight)
        processText(doc.getTitle(), terms, TextSection.TITLE);
        System.out.println("Processing title done");

        // Process headings (medium weight)
        processText(doc.getHeadings(), terms, TextSection.HEADINGS);
        System.out.println("Processing headings done");

        // Process body (normal weight)
        processText(doc.getBody(), terms, TextSection.BODY);
        System.out.println("Processing body done");

        // Update inverted index in MongoDB
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
                                    .append("title_positions", termData.getTitlePositions())
                                    .append("headings_positions", termData.getHeadingsPositions())
                                    .append("body_positions", termData.getBodyPositions())
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

    private enum TextSection {
        TITLE, HEADINGS, BODY
    }

    private void processText(String text, Map<String, Term> terms, TextSection section) {
        List<String> tokens = tokenizer.tokenize(text);

        for (int position = 0; position < tokens.size(); position++) {
            String token = tokens.get(position);
            Term term = terms.getOrDefault(token, new Term(token));

            switch (section) {
                case TITLE:
                    term.incrementTfTitle(position);
                    break;
                case HEADINGS:
                    term.incrementTfHeadings(position);
                    break;
                case BODY:
                    term.incrementTfBody(position);
                    break;
            }

            terms.put(token, term);
        }
    }

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
                    docEntry.getDouble("tf_body"),
                    docEntry.getList("title_positions", Integer.class),
                    docEntry.getList("headings_positions", Integer.class),
                    docEntry.getList("body_positions", Integer.class)
            );
        }

        return entry;
    }

    public int getDocumentCount() {
        return (int) documentsCollection.countDocuments();
    }
}