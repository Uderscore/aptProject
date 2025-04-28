package queryProcessor;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import utilities.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QueryProcessor {

    private Set<String> stopWords;
    private Stemmer stemmer;
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
        query = query.toLowerCase();
        query = query.replaceAll("[^0-9a-zA-Z]", " ");
        String[] words = query.split(" ");
        ArrayList<String> processedQuery = new ArrayList<>();
        for (String word : words) {
            if (word.trim().isEmpty() || stopWords.contains(word))
                continue;
            String stemmedWord = stem(word);
            if (stemmedWord != null)
                processedQuery.add(stemmedWord);
        }
        return processedQuery;
    }

    public Map<String, String> processWithOriginals(String query) {
        query = query.toLowerCase();
        query = query.replaceAll("[^0-9a-zA-Z]", " ");
        String[] words = query.split(" ");
        Map<String, String> wordMap = new LinkedHashMap<>();
        for (String word : words) {
            if (word.trim().isEmpty() || stopWords.contains(word))
                continue;
            String stemmed = stem(word);
            if (stemmed != null)
                wordMap.put(word, stemmed);
        }
        return wordMap;
    }

    public String processWord(String word) {
        if (word.trim().isEmpty() || stopWords.contains(word))
            return null;
        return stem(word);
    }

    // public void extractPersonName(String country, String query,
    // PersonNameThread.PersonNameListener listener) {
    // if (classifier == null)
    // loadClassifier();
    // new PersonNameThread(classifier, country, query, listener).start();
    // }

    private void initStopWords() {
        stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.STOP_WORDS_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Failed to load stop words.");
            e.printStackTrace();
        }
    }

    private void loadClassifier() {
        if (classifier != null)
            return;
        String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
        try {
            classifier = CRFClassifier.getClassifier(serializedClassifier);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load Stanford NER classifier.");
            e.printStackTrace();
        }
    }
}
