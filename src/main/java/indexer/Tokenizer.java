package indexer;

import java.util.ArrayList;
import java.util.List;
import org.tartarus.snowball.ext.PorterStemmer;

public class Tokenizer {

    /**
     * Tokenizes the input text into a list of tokens.
     * This method performs basic tokenization and stemming.
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
        // for example: "Hello, world!" -> ["hello", "world"]
        String[] words = text.toLowerCase().split("[\\s\\p{Punct}]+");

        // Stemming (process of reducing a word to its base or root form)
        // For example: "running" -> "run", "better" -> "better"
        // I'm searching for a library that can ignore the stop words or the non-valuable words, so it will be updated later
        PorterStemmer stemmer = new PorterStemmer();
        for (String word : words) {
            if (word.length() > 0) {
                stemmer.setCurrent(word);
                stemmer.stem();
                tokens.add(stemmer.getCurrent());
            }
        }

        return tokens;
    }
}