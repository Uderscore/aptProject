package indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tartarus.snowball.ext.PorterStemmer;
import utilities.Constants;

public class Tokenizer {
    private Set<String> stopWords;

    public Tokenizer() {
        initStopWords();
    }

    /**
     * Tokenizes the input text into a list of tokens.
     * This method performs basic tokenization, stop word filtering, and stemming.
     *
     * @param text The input text to tokenize.
     * @return A list of tokens extracted from the input text.
     */
    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return tokens;
        }

        // Simple tokenization - split on whitespace and punctuation
        text = text.toLowerCase().replaceAll("[^0-9a-zA-Z]", " ");
        String[] words = text.split("\\s+");

        // Stemming and stop word filtering
        PorterStemmer stemmer = new PorterStemmer();
        for (String word : words) {
            if (word.trim().isEmpty() || stopWords.contains(word) || word.trim().length() < 2) {
                continue;
            }

            stemmer.setCurrent(word);
            stemmer.stem();
            tokens.add(stemmer.getCurrent());
        }

        return tokens;
    }

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
}