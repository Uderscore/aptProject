package ranker.utils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import indexer.utils.MongoConnector;
import org.bson.Document;
import reactor.core.publisher.Mono;

import java.util.*;

public class PageRankAlgo {
    private static final double DAMPING_FACTOR = .85;
    private static final int MAX_ITERATIONS = 55;
    private static final double CONVERGENCE_THRESHOLD = 0.0001;
    private final Map<String, Integer> urlToIdMap;
    private final Map<Integer, String> idToUrlMap;
    private List<Set<Integer>> adjList;
    private double[] pageRankScores;
    private final MongoCollection<Document> documentsCollection;


    public PageRankAlgo(MongoCollection<Document> documentsCollection) {
        urlToIdMap = new HashMap<>();
        idToUrlMap = new HashMap<>();
        adjList = new ArrayList<>();
        pageRankScores = new double[0];
        this.documentsCollection = documentsCollection;
    }

    public void computePageRank() {
        convertUrlsToIds();
        if (urlToIdMap.isEmpty()) {
            System.out.println("No URLs found in the collection.");
            return;
        }

        initAdjList();
        populateAdjList();
        calcRanks();
        storeNewRanksInDB();
    }

    private void convertUrlsToIds() {
        int idCounter = 0;
        try (MongoCursor<Document> cursor = documentsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String url = doc.getString("url");
                if (url == null || url.isEmpty() || urlToIdMap.containsKey(url)) continue;
                urlToIdMap.put(url, idCounter);
                idToUrlMap.put(idCounter, url);
                idCounter++;
            }
        } catch (Exception e) {
            System.err.println("Error fetching documents: " + e.getMessage());
        }
    }

    private void initAdjList() {
        int nodes = urlToIdMap.size();
        adjList = new ArrayList<>(nodes);
        for (int i = 0; i < nodes; i++) {
            adjList.add(new HashSet<>());
        }
    }

    private void populateAdjList() {
        try (MongoCursor<Document> cursor = documentsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String url = doc.getString("url");
                List<String> outgoingLinks = (List<String>) doc.get("outgoingLinks");

                if (outgoingLinks == null || outgoingLinks.isEmpty()) continue;
                if (url == null || url.isEmpty()) continue;
                int fromNode = urlToIdMap.get(url);
                for (String link : outgoingLinks) {
                    if (!urlToIdMap.containsKey(link)) continue;
                    int toNode = urlToIdMap.get(link);
                    if (adjList.get(fromNode).contains(toNode)) continue;
                    adjList.get(fromNode).add(toNode);
                }
            }
        }
    }


    /**
     * this method is used to calculate the ranks of the pages
     * it uses the page rank algorithm
     * it is an iterative algorithm for maxIterations times or until convergence
     * it uses the damping factor to calculate the rank of each page about .85 to avoid SCC, Not all Connected Graphs
     * it used the contribution of each page to the pages it links to
     * it used the equations pi(n + 1) = (1 - d) / N + d * sum(pi(i) / L(i))
     */
    private void calcRanks() {
        int numOfNode = adjList.size();
        pageRankScores = new double[numOfNode];
        Arrays.fill(pageRankScores, 1.0 / numOfNode);


        for (int iter = 0; iter < MAX_ITERATIONS; ++iter) {
            double[] nextPageRankScores = new double[numOfNode];
            Arrays.fill(nextPageRankScores, (1 - DAMPING_FACTOR) / numOfNode);
            double danglingMass = 0.0;


            for (int i = 0; i < numOfNode; ++i) {
                if (adjList.get(i).isEmpty()) {
                    danglingMass += pageRankScores[i];
                    continue;
                }
                double contribution = DAMPING_FACTOR * (pageRankScores[i] / adjList.get(i).size());
                for (int j : adjList.get(i)) {
                    nextPageRankScores[j] += contribution;
                }
            }

            double distributedRank = DAMPING_FACTOR * (danglingMass / numOfNode);
            for (int i = 0; i < numOfNode; ++i) {
                nextPageRankScores[i] += distributedRank;
            }

            // check for convergence
            double error = 0.0;
            for (int i = 0; i < numOfNode; ++i) {
                error += Math.abs(nextPageRankScores[i] - pageRankScores[i]);
            }

            if (error < CONVERGENCE_THRESHOLD) {
                return;
            }

            // update the page rank scores
            pageRankScores = nextPageRankScores;
        }
        System.out.println("Could not converge in " + MAX_ITERATIONS + " iterations.");
    }


    private  void storeNewRanksInDB() {
        List<WriteModel<Document>> bulkWrites = new ArrayList<>();
        final int BATCH_SIZE = 1000;

        try {
            for (int i = 0; i < pageRankScores.length; i++) {
                String url = idToUrlMap.get(i);
                double rank = pageRankScores[i];

                bulkWrites.add(new UpdateOneModel<>(
                        Filters.eq("url", url),
                        Updates.set("popularity", rank)
                ));

                // Process in batches of 1000
                if (bulkWrites.size() >= BATCH_SIZE) {
                    documentsCollection.bulkWrite(bulkWrites);
                    bulkWrites.clear();
                }
            }

            // Process remaining updates
            if (!bulkWrites.isEmpty()) {
                documentsCollection.bulkWrite(bulkWrites);
            }
        } catch (Exception e) {
            System.out.println("Error updating documents: " + e.getMessage());
        }
    }
}