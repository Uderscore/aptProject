package indexer;

import indexer.models.Document;
import indexer.utils.MongoConnector;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexManager {
    // Thread-safe singleton pattern
    private final ExecutorService executor;
    private final MongoIndexer indexer;
    private final DocumentProcessor docProcessor;
    private final AtomicInteger taskCount = new AtomicInteger(0);

    public IndexManager(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.indexer = new MongoIndexer();
        this.docProcessor = new DocumentProcessor();
    }

    public static String fetchHtmlContent(String url) {
        try {
            org.jsoup.nodes.Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void indexAsync(String url) {
        String htmlContent = fetchHtmlContent(url);
        if (htmlContent == null) {
            System.err.println("Failed to fetch content for URL: " + url);
            return;
        }

        taskCount.incrementAndGet();
        // Using a thread pool to handle multiple indexing tasks concurrently
        executor.submit(() -> {
            try {
                if (!indexer.isUrlIndexed(url)) {
                    Document doc = docProcessor.process(url, htmlContent);
                    indexer.indexDocument(doc);
                    System.out.println("Indexed: " + url);
                } else {
                    System.out.println("Skipped duplicate: " + url);
                }
            } catch (Exception e) {
                System.err.println("Failed to index " + url + ": " + e.getMessage());
            } finally {
                taskCount.decrementAndGet();
            }
        });
    }

    public void awaitCompletion() throws InterruptedException {
        // Wait for existing tasks to finish
        while (taskCount.get() > 0) {
            Thread.sleep(500);
        }
    }

    public void shutdown() {
        try {
            // Graceful shutdown
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            MongoConnector.close();
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}