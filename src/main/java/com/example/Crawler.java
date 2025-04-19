package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler {

    private static final int MAX_DEPTH = 3;
    private static final int THREAD_COUNT = 100;

    // Thread-safe data structures
    private static final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private static final List<String[]> visitedUrlsList = Collections.synchronizedList(new ArrayList<>());
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private static final AtomicInteger activeTasks = new AtomicInteger(0);

    public static void main(String[] args) {
        String seedUrl = "https://en.wikipedia.org/wiki/Web_crawler";

        activeTasks.incrementAndGet(); // start tracking the root task
        crawl(seedUrl, 1);

        // Wait until all crawling tasks are completed
        while (activeTasks.get() > 0) {
            try {
                Thread.sleep(100); // Wait a short while before checking again
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        shutdownExecutorService();
        exportDataToCsv("Urls.txt");
    }

    private static void crawl(String url, int depth) {
        String normalizedUrl = normalizeUrl(url);
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            System.err.println("Skipping invalid URL: " + normalizedUrl);
            activeTasks.decrementAndGet();
            return;
        }

        if (depth > MAX_DEPTH || !visitedUrls.add(normalizedUrl)) {
            activeTasks.decrementAndGet();
            return;
        }

        executorService.submit(() -> {
            try {
                System.out.println("Crawling: " + normalizedUrl);
                visitedUrlsList.add(new String[]{normalizedUrl});

                Document doc = retrieveHTML(normalizedUrl);
                if (doc != null) {
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = normalizeUrl(link.absUrl("href"));
                        if (!nextUrl.isEmpty() && !visitedUrls.contains(nextUrl)) {
                            activeTasks.incrementAndGet();
                            crawl(nextUrl, depth + 1);
                        }
                    }
                }
            } finally {
                activeTasks.decrementAndGet(); // task complete
            }
        });
    }

    private static Document retrieveHTML(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Page Title: " + doc.title());
            return doc;
        } catch (IOException e) {
            System.err.println("Unable to fetch HTML of: " + url);
            return null;
        }
    }

    private static String normalizeUrl(String url) {
        try {
            java.net.URI uri = new java.net.URI(url).normalize();
            String normalized = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
            return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
        } catch (Exception e) {
            return url;
        }
    }

    private static void exportDataToCsv(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("URL\n");
            synchronized (visitedUrlsList) {
                for (String[] row : visitedUrlsList) {
                    writer.append(String.join(",", row)).append("\n");
                }
            }
            System.out.println("Data saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                System.out.println("Forced shutdown due to timeout.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
