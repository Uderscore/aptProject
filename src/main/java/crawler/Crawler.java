package crawler;

import com.mongodb.client.*;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.IndexOptions;

public class Crawler {
    private static BufferedWriter blockedUrlsWriter;
    private static final int MAX_DEPTH = 2;
    private static ExecutorService executorService;
    private static final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger activeTasks = new AtomicInteger(0);
    private static final Map<String, Set<String>> disallowedPathsByDomain = new ConcurrentHashMap<>();

    private static MongoCollection<Document> visitedCollection;

    public static void main(String[] args) {
        int threadCount = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        System.out.println("Using " + threadCount + " threads for crawling.");
        executorService = Executors.newFixedThreadPool(threadCount);

        // MongoDB connection
        MongoClient mongoClient = MongoClients.create("mongodb+srv://mariohabib04:VihXqRIepdEtfxd4@search-engine.3p2dfao.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("search-engine");
        visitedCollection = database.getCollection("urls");

        // Ensure unique index on 'url'
        visitedCollection.createIndex(new Document("url", 1), new IndexOptions().unique(true));

        // Load visited URLs from MongoDB
        for (Document doc : visitedCollection.find()) {
            String url = doc.getString("url");
            if (url != null) visitedUrls.add(url);
        }

        try {
            blockedUrlsWriter = new BufferedWriter(new FileWriter("blocked_urls.txt", true));
        } catch (IOException e) {
            System.err.println("Could not open file to log blocked URLs.");
            return;
        }

        List<String> seeds = loadUrlsFromFile("seeds.txt");
        for (String seed : seeds) {
            String normalized = normalizeUrl(seed);
            if (!visitedUrls.contains(normalized)) {
                activeTasks.incrementAndGet();
                crawl(normalized, 1);
            }
        }

        while (activeTasks.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        executorService.shutdown();
        System.out.println("Crawling complete.");
    }

    private static void crawl(String url, int depth) {
        String normalizedUrl = normalizeUrl(url);

        if (depth > MAX_DEPTH || !normalizedUrl.startsWith("http") || !visitedUrls.add(normalizedUrl)) {
            activeTasks.decrementAndGet();
            return;
        }

        if (!isAllowedByRobotsTxt(normalizedUrl)) {
            System.out.println("Blocked by robots.txt: " + normalizedUrl);
            try {
                synchronized (blockedUrlsWriter) {
                    blockedUrlsWriter.write(normalizedUrl);
                    blockedUrlsWriter.newLine();
                    blockedUrlsWriter.flush();
                }
            } catch (IOException e) {
                System.err.println("Failed to write blocked URL: " + normalizedUrl);
            }
            activeTasks.decrementAndGet();
            return;
        }

        executorService.submit(() -> {
            try {
                System.out.println("Crawling: " + normalizedUrl);
                saveUrlToDatabase(normalizedUrl);

                org.jsoup.nodes.Document doc = retrieveHTML(normalizedUrl);
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
                activeTasks.decrementAndGet();
            }
        });
    }

    private static org.jsoup.nodes.Document retrieveHTML(String url) {
        try {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        } catch (IOException e) {
            System.err.println("Failed to fetch: " + url);
            return null;
        }
    }

    private static String normalizeUrl(String url) {
        try {
            URI uri = new URI(url).normalize();
            String scheme = (uri.getScheme() == null ? "http" : uri.getScheme().toLowerCase());
            String host = (uri.getHost() == null ? "" : uri.getHost().toLowerCase());
            if (host.startsWith("www.")) host = host.substring(4);

            String path = uri.getPath() == null || uri.getPath().isEmpty() ? "/" : uri.getPath();
            if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            String query = (uri.getQuery() == null ? "" : "?" + uri.getQuery());
            return scheme + "://" + host + path + query;
        } catch (Exception e) {
            return url;
        }
    }

    private static boolean isAllowedByRobotsTxt(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String scheme = uri.getScheme();
            String robotsUrl = scheme + "://" + host + "/robots.txt";

            if (!disallowedPathsByDomain.containsKey(host)) {
                Set<String> disallowed = new HashSet<>();
                try {
                    String robotsTxt = Jsoup.connect(robotsUrl)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0")
                            .timeout(5000)
                            .execute()
                            .body();

                    boolean appliesToUs = false;
                    for (String line : robotsTxt.split("\n")) {
                        line = line.trim();
                        if (line.toLowerCase().startsWith("user-agent:")) {
                            String agent = line.substring(11).trim();
                            appliesToUs = agent.equals("*");
                        } else if (appliesToUs && line.toLowerCase().startsWith("disallow:")) {
                            String path = line.substring(9).trim();
                            if (path.isEmpty()) {
                                disallowed.clear();  // allow all
                            } else {
                                if (!path.startsWith("/")) path = "/" + path;
                                disallowed.add(path.trim());
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to fetch robots.txt from: " + robotsUrl);
                }
                disallowedPathsByDomain.put(host, disallowed);
            }

            String path = uri.getPath();
            for (String disallowed : disallowedPathsByDomain.getOrDefault(host, Collections.emptySet())) {
                if (path.startsWith(disallowed)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return true;  // Allow if any error
        }
    }

    private static void saveUrlToDatabase(String url) {
        if (visitedCollection.find(eq("url", url)).first() == null) {
            visitedCollection.insertOne(new Document("url", url));
        }
    }

    private static List<String> loadUrlsFromFile(String filePath) {
        List<String> urls = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String url = scanner.nextLine().trim();
                if (!url.isEmpty()) urls.add(url);
            }
        } catch (IOException e) {
            // Ignore on first run
        }
        return urls;
    }
}
