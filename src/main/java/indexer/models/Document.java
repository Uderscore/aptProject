package indexer.models;
import java.util.ArrayList;
import java.util.List;

public class Document {
    private String url;
    private String title;
    private String headings;
    private String body;
    private double popularity;

    private List<String> outgoingLinks;

    public Document() {
        this.outgoingLinks = new ArrayList<>();
    }


    // Getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHeadings() { return headings; }
    public void setHeadings(String headings) { this.headings = headings; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<String> getOutgoingLinks() { return outgoingLinks; }
    public void setOutgoingLinks(List<String> outgoingLinks) { this.outgoingLinks = outgoingLinks; }

    public double getPopularity() { return popularity; }
    public void setPopularity(double popularity) { this.popularity = popularity; }

}