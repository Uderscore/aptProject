package indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import indexer.models.Document;

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

        // Extract body text
        String bodyText = parsedDoc.body().text();
        doc.setBody(bodyText);

        return doc;
    }
}