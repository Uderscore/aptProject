package ranker.rankers;

import com.mongodb.client.MongoCollection;
import indexer.MongoIndexer;
import indexer.models.DocumentTermInfo;
import indexer.models.InvertedIndexEntry;
import indexer.utils.MongoConnector;
import ranker.interfaces.IRanker;
import ranker.models.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ranker.utils.GetNthLargest;


public class Ranker implements IRanker {
    private  static MongoIndexer indexer = null;
    private final static double ALPHA = 0.70;
    private final static int TITLE_WEIGHT = 3;
    private final static int HEADING_WEIGHT = 2;
    private final static int BODY_WEIGHT = 1;
    private  static MongoCollection<org.bson.Document> documentCollection = null;
    private static int documentCount;

    public Ranker() {
        MongoConnector.initialize();
        indexer = new MongoIndexer();
        documentCollection = MongoConnector.getCollection("documents");

        documentCount = indexer.getDocumentCount();
        if (documentCount == 0) {
            System.err.println("No documents indexed. Cannot perform ranking.");
        }
    }




    @Override
    public List<String> rank(List<String> terms, int topK) {
        if (documentCount == 0) {
            System.err.println("No documents indexed. Cannot perform ranking.");
            return List.of();
        }

        Map<String, Double> documentScore = new HashMap<>();//tf-Idf popularity
        Map<String, Double> popularity = new HashMap<>();
        terms.forEach(term -> calculateTF_IDF(term, documentScore));
        calculatePageRank(popularity);

        for (Map.Entry<String, Double> doc : documentScore.entrySet()) {
            String url = doc.getKey();
            System.out.println("url is " + url);
            System.out.println("score is " + doc.getValue());
        }


        return GetNthLargest.getNthElements(resultsSizedK(documentScore, topK), Math.min(topK, documentScore.size()));
    }


    private void calculatePageRank(Map<String, Double> documentScore) {


        for (Map.Entry<String, Double> doc : documentScore.entrySet()) {
            String url = doc.getKey();
            org.bson.Document document = documentCollection.find(com.mongodb.client.model.Filters.eq("url", url)).first();
            if (document != null && document.containsKey("popularity")) {
                double scorePopularity = document.getDouble("popularity");
                documentScore.put(url, finalScore(doc.getValue(), scorePopularity));
            } else {
                System.err.println("Document not found for URL: " + url);
            }
        }
    }

    private static double  finalScore(double scoreRelevance, double scorePopularity) {
        return (ALPHA * scoreRelevance) + ((1 - ALPHA) * scorePopularity);
    }

    List<Pair<String, Double>> resultsSizedK(Map<String, Double> documentScore, int topK) {
        List<Pair<String, Double>> result = new ArrayList<>(Math.min(documentScore.size(), topK));

        for (Map.Entry<String, Double> doc : documentScore.entrySet()) {
            String url = doc.getKey();
            double score = doc.getValue();
            result.add(new Pair<>(url, score));
            if (result.size() == topK) {
                break;
            }
        }
        System.out.printf("Result size is %d\n", result.size());
        System.out.println("Result is " + result);

        return result;
    }

    private void calculateTF_IDF(String term, Map<String, Double> documentScore) {
        InvertedIndexEntry entry = indexer.getDocumentsForTerm(term);
        System.out.println("Iam in the Ranker ");

        if (entry == null)  return;

        int df = entry.getDocumentFrequency();

        if (df == 0) return;

        System.out.println("_".repeat(20));
        System.out.println("Term is " + term);
        System.out.println("Document frequency is " + df);
        System.out.println("Document count is " + documentCount);
        System.out.println("_".repeat(20));
        double idf = Math.log10((double) documentCount / df);

        var termDocuments = entry.getDocuments();

        for (Map.Entry<String, DocumentTermInfo> doc : termDocuments.entrySet()) {
            DocumentTermInfo termInfo = doc.getValue();
            String url = doc.getKey();

            double tf_title = termInfo.getTfTitle();
            double tf_heading = termInfo.getTfHeadings();
            double tf_body = termInfo.getTfBody();
            double TF = (TITLE_WEIGHT * tf_title + HEADING_WEIGHT * tf_heading + BODY_WEIGHT * tf_body);
            double TFIDF = TF * idf;
            System.out.println("Tf is " + TF);
            System.out.println("Idf is " + idf);
            System.out.println("TFIDF is " + TFIDF);
            documentScore.put(url, documentScore.getOrDefault(url, 0.0) + TFIDF);
        }
        System.out.println("There");
    }
}
