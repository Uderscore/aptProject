package ranker.interfaces;

import java.util.List;

public interface IRanker {
    List<String> rank(List<String> urls, int topK);
}
