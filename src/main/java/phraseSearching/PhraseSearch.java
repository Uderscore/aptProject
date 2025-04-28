// package phraseSearching;

// import model.TextSearchResult;
// import queryprocessor.QueryProcessor;
// import ranker.Ranker;

// import java.util.*;
// import java.util.concurrent.*;

// public class PhraseSearch {
// public static List<TextSearchResult> search(Ranker ranker, String
// phraseQuery, String country,
// List<String> stemmedWords) {
// QueryProcessor processor = QueryProcessor.getInstance(null); // already
// initialized
// List<TextSearchResult> results = ranker.rankPhrase(stemmedWords, country);

// PhraseQueryParser.PhraseQuery parsed = PhraseQueryParser.parse(phraseQuery);

// List<KMPMatcher> includeMatchers = new ArrayList<>();
// List<KMPMatcher> excludeMatchers = new ArrayList<>();

// for (String phrase : parsed.include) {
// includeMatchers.add(new KMPMatcher(processor.process(phrase)));
// }
// for (String phrase : parsed.exclude) {
// excludeMatchers.add(new KMPMatcher(processor.process(phrase)));
// }

// ConcurrentMap<String, Boolean> found = new ConcurrentHashMap<>();
// BlockingQueue<String> queue = new LinkedBlockingQueue<>();
// results.forEach(r -> queue.offer(r.getUrl()));

// int threads = Math.max(1, Math.min(20, results.size() / 1000));
// ExecutorService pool = Executors.newFixedThreadPool(threads);

// for (int i = 0; i < threads; i++) {
// pool.execute(new PhraseSearchTask(queue, found, includeMatchers,
// excludeMatchers, processor));
// }

// pool.shutdown();
// try {
// pool.awaitTermination(60, TimeUnit.SECONDS);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }

// List<TextSearchResult> filtered = new ArrayList<>();
// for (TextSearchResult r : results) {
// if (found.containsKey(r.getUrl())) {
// filtered.add(r);
// }
// }

// return filtered;
// }
// }
