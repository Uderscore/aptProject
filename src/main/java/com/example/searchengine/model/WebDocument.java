package com.example.searchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "documents")
public class WebDocument {
    @Id
    private String id;
    private String url;
    private String title;
//    private String body;
    private List<String> headings;

    private String body;
    private double popularity;


    // Constructors, getters, setters
    public WebDocument() {}

    public double getPopularity() { return popularity; }
    public void setPopularity(double popularity) { this.popularity = popularity; }
    public String getBody() { return body; }
    public void setSnippet(String body) { this.body = body; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

//    public String getContent() { return body; }
//    public void setContent(String body) { this.body = body; }

    public List<String> getHeadings() { return headings; }
    public void setHeadings(List<String> headings) { this.headings = headings; }
}