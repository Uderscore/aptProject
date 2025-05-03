package indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import indexer.models.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentProcessor {

    /**
     * Processes the HTML content of a webpage and extracts relevant information.
     *
     * @param url         The URL of the webpage.
     * @param htmlContent The HTML content of the webpage.
     * @return A Document object containing the extracted information.
     */
    public Document process(String url, String htmlContent) {
        Document doc = new Document();
        doc.setUrl(url);

        org.jsoup.nodes.Document parsedDoc = Jsoup.parse(htmlContent);

        // Extract title
        String title = parsedDoc.title();
        doc.setTitle(title);

        // Extract headings (h1-h6)
        Elements headings = parsedDoc.select("h1, h2, h3, h4, h5, h6");
        StringBuilder headingText = new StringBuilder();
        for (Element heading : headings) {
            headingText.append(heading.text()).append(" ");
        }
        doc.setHeadings(headingText.toString().trim());


        //getting the outLinks for building the LinkGraph for outgoing
        Elements links = parsedDoc.select("a[href]");
        List<String> outgoingLinks = new ArrayList<>();
        for (Element link: links) {
            String absUrl = link.absUrl("href");
            if (absUrl.startsWith("http")) {
                outgoingLinks.add(absUrl);
            }
        }
        doc.setOutgoingLinks(outgoingLinks);
        doc.setPopularity(0.0); // Placeholder for popularity, can be updated later


        // Extract body text
        String bodyText = parsedDoc.body().text();
        doc.setBody(bodyText);

        int wordsCount = Tokenizer.removeNonAlphanumeric(doc.getTitle()).length +
                Tokenizer.removeNonAlphanumeric(doc.getHeadings()).length +
                Tokenizer.removeNonAlphanumeric(doc.getBody()).length;
        doc.setWordCount(wordsCount);

        return doc;
    }
}