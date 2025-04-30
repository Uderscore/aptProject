// package phraseSearching;

// import utilities.ConnectToDB;
// import queryProcessor.QueryProcessor;

// import java.util.List;
// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.ConcurrentMap;

// public class PhraseSearchTask implements Runnable {
// private final BlockingQueue<String> queue;
// private final ConcurrentMap<String, Boolean> found;
// private final List<KMPMatcher> includeMatchers;
// private final List<KMPMatcher> excludeMatchers;
// private final QueryProcessor processor;

// public PhraseSearchTask(BlockingQueue<String> queue,
// ConcurrentMap<String, Boolean> found,
// List<KMPMatcher> includeMatchers,
// List<KMPMatcher> excludeMatchers,
// QueryProcessor processor) {
// this.queue = queue;
// this.found = found;
// this.includeMatchers = includeMatchers;
// this.excludeMatchers = excludeMatchers;
// this.processor = processor;
// }

// @Override
// public void run() {
// while (!queue.isEmpty()) {
// String url = queue.poll();
// if (url == null)
// continue;

// String text = ConnectToDB.getText(url);
// if (text == null)
// continue;

// List<String> tokens = processor.process(text);
// boolean includeMatch = includeMatchers.stream().allMatch(matcher ->
// matcher.matches(tokens));
// boolean excludeMatch = excludeMatchers.stream().anyMatch(matcher ->
// matcher.matches(tokens));

// if (includeMatch && !excludeMatch) {
// found.put(url, true);
// }
// }
// }
// }
