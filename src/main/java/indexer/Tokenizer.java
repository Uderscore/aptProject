package indexer;
import java.util.List;
import queryProcessor.QueryProcessor;
import queryProcessor.StanfordLemmatizerImpl;

public class Tokenizer {
    private static final QueryProcessor queryProcessor = QueryProcessor.getInstance(new StanfordLemmatizerImpl());

    /**
     * Tokenizes the input text into a list of tokens.
     * This method performs basic tokenization, stop word filtering, and stemming.
     *
     * @param text The input text to tokenize.
     * @return A list of tokens extracted from the input text.
     */
    public static List<String> tokenize(String text) {
        return queryProcessor.process(text);
    }
}