package indexer.models;

public class Document {
    private String url;
    private String title;
    private String headings;
    private String body;

    // Getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHeadings() { return headings; }
    public void setHeadings(String headings) { this.headings = headings; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}