package phraseSearching;

import java.util.*;
import java.util.regex.*;

public class PhraseQueryParser {
    public static class PhraseQuery {
        public List<String> include = new ArrayList<>();
        public List<String> exclude = new ArrayList<>();
    }

    public static PhraseQuery parse(String input) {
        PhraseQuery pq = new PhraseQuery();

        // Extract phrases
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(input);
        List<String> phrases = new ArrayList<>();
        while (matcher.find()) {
            phrases.add(matcher.group(1));
        }

        if (input.contains("AND NOT")) {
            pq.include.add(phrases.get(0));
            pq.exclude.add(phrases.get(1));
        } else if (input.contains("OR")) {
            pq.include.addAll(phrases);
        } else if (input.contains("AND")) {
            pq.include.addAll(phrases);
        } else {
            pq.include.addAll(phrases);
        }

        return pq;
    }
}
