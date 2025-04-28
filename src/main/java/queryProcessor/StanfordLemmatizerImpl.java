package queryProcessor;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.*;

import java.util.*;

public class StanfordLemmatizerImpl implements Stemmer {
    private final StanfordCoreNLP pipeline;

    public StanfordLemmatizerImpl() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public String stem(String word) {
        Annotation annotation = new Annotation(word);
        pipeline.annotate(annotation);
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                return token.get(CoreAnnotations.LemmaAnnotation.class);
            }
        }
        return word;
    }
}
