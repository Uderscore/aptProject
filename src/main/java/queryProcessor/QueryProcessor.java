package queryProcessor;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import ranker.rankers.Ranker;

import java.io.*;
import java.util.*;

public class QueryProcessor {

    private Set<String> stopWords;
    private final Stemmer stemmer;
    private AbstractSequenceClassifier<CoreLabel> classifier;
    private static QueryProcessor instance;

    private QueryProcessor(Stemmer stemmerImpl) {
        this.stemmer = stemmerImpl;
        initStopWords();
        loadClassifier();
    }

    public static QueryProcessor getInstance(Stemmer stemmerImpl) {
        if (instance != null)
            return instance;
        instance = new QueryProcessor(stemmerImpl);
        return instance;
    }

    public String stem(String word) {
        return stemmer.stem(word);
    }

    public List<String> process(String query) {
        String[] words = removeNonAlphaNumerics(query);

        ArrayList<String> processedQuery = new ArrayList<>();
        for (String word : words) {
            if (word.trim().isEmpty() || stopWords.contains(word) || word.trim().length() < 2)
                continue;
            String stemmedWord = stem(word);
            if (stemmedWord != null)
                processedQuery.add(stemmedWord);
        }
        return processedQuery;
    }

    public String[] removeNonAlphaNumerics(String query) {
        query = query.toLowerCase();
        query = query.replaceAll("[^0-9a-zA-Z]", " ");
        query = query.replaceAll("\\s+", " ");
        return query.split(" ");
    }


    private void initStopWords() {
        stopWords = new HashSet<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("stop_words.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stopWords.add(line.trim().toLowerCase());
                    }
                }
            } else {
                System.err.println("Stop words file not found in resources");
                // Fallback to default stop words
                Collections.addAll(stopWords,
                        "a", "an", "the", "this", "that", "is", "are", "was", "were", "be", "been");
            }
        } catch (IOException e) {
            System.err.println("Error loading stop words: " + e.getMessage());
            // Fallback to default stop words
            Collections.addAll(stopWords,
                    "a", "an", "the", "this", "that", "is", "are", "was", "were", "be", "been");
        }
    }

    private void loadClassifier() {
        if (classifier != null) return;

        try {
            // Load from the models JAR
            classifier = CRFClassifier.getClassifier(
                    "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load Stanford NER classifier: " + e.getMessage());
            e.printStackTrace();

            // Alternative loading method
            try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream(
                    "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz")) {
                if (modelIn != null) {
                    classifier = CRFClassifier.getClassifier(modelIn);
                }
            } catch (IOException | ClassNotFoundException e2) {
                System.err.println("Alternative loading also failed: " + e2.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        List<String> processedQuery = new ArrayList<>();
        try {
            String query = "cake";
            QueryProcessor queryProcessor = QueryProcessor.getInstance(new StanfordLemmatizerImpl());
            processedQuery = queryProcessor.process(query);
            System.out.println("Processed Query: " + processedQuery);
        } catch (Exception e) {
            System.out.println("fk");
        }
        Ranker ranker = new Ranker();
        List<String> rankerUrls = ranker.rank(processedQuery, 10000);
        System.out.println("Ranked URLs: " + rankerUrls);
    }
}

