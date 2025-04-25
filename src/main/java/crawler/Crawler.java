package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler {

    private static final int MAX_DEPTH = 2;
    private static ExecutorService executorService;
    private static final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger activeTasks = new AtomicInteger(0);
    private static final Map<String, Set<String>> disallowedPathsByDomain = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int threadCount = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        executorService = Executors.newFixedThreadPool(threadCount);

        List<String> seeds = loadUrlsFromFile("seeds.txt");
        visitedUrls.addAll(loadUrlsFromFile("visited.txt"));

        for (String seed : seeds) {
            String normalized = normalizeUrl(seed);
            if (!visitedUrls.contains(normalized)) {
                activeTasks.incrementAndGet();
                crawl(normalized, 1);
            }
        }

        while (activeTasks.get() > 0) {
            try { Thread.sleep(100); } catch (InterruptedException e) {
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
            activeTasks.decrementAndGet();
            return;
        }

        executorService.submit(() -> {
            try {
                System.out.println("Crawling: " + normalizedUrl);
                saveUrlToFile("visited.txt", normalizedUrl);

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
                activeTasks.decrementAndGet();
            }
        });
    }

    private static Document retrieveHTML(String url) {
        try {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        } catch (IOException e) {
            System.err.println("Failed to fetch: " + url);
            return null;
        }
    }

    private static String normalizeUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme().toLowerCase();
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
            if (host.startsWith("www.")) host = host.substring(4);
            String path = uri.getPath() == null ? "" : uri.getPath();
            if (path.endsWith("/") && path.length() > 1) path = path.substring(0, path.length() - 1);
            return scheme + "://" + host + path;
        } catch (Exception e) {
            return url;
        }
    }

    private static boolean isAllowedByRobotsTxt(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String robotsUrl = uri.getScheme() + "://" + host + "/robots.txt";

            if (!disallowedPathsByDomain.containsKey(host)) {
                Set<String> disallowed = new HashSet<>();
                try {
                    Document doc = Jsoup.connect(robotsUrl).ignoreContentType(true).get();
                    String[] lines = doc.body().text().split("User-agent: \\*");
                    if (lines.length > 1) {
                        for (String line : lines[1].split("\n")) {
                            if (line.toLowerCase().startsWith("disallow:")) {
                                disallowed.add(line.split(":", 2)[1].trim());
                            }
                        }
                    }
                } catch (IOException ignored) {}
                disallowedPathsByDomain.put(host, disallowed);
            }

            String path = uri.getPath();
            for (String disallowed : disallowedPathsByDomain.get(host)) {
                if (path.startsWith(disallowed)) return false;
            }
            return true;

        } catch (Exception e) {
            return true; // default to allow
        }
    }

    private static void saveUrlToFile(String filePath, String url) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(url + "\n");
        } catch (IOException e) {
            e.printStackTrace();
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
            // File not found is okay on first run
        }
        return urls;
    }
}
