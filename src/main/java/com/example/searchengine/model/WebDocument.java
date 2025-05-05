//package com.example.searchengine.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Transient;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.io.Serial;
//import java.io.Serializable;
//
//
//@Document(collection = "documents")
//public class WebDocument implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//    @Id
//    private String id;
//    @Indexed
//    private String url;
////    private String body;
//    private String title;
//
//    @Transient
//    private String snippet;
//
//    @JsonIgnore // Ignore this field during serialization
//    private String headings;
//
//
//    @JsonIgnore // Ignore this field during serialization
//    private String body;
//
//
//    @JsonIgnore // Ignore this field during serialization
//    private double popularity;
//
//    @JsonIgnore // Ignore this field during serialization
//    private int wordCount;
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    public String getSnippet() {
//        return snippet;
//    }
//
//
//
//    public int getWordCount() {
//        return wordCount;
//    }
//
//    public void setWordCount(int wordCount) {
//        this.wordCount = wordCount;
//    }
//
//    // Constructors, getters, setters
//    public WebDocument() {}
//
//    public double getPopularity() { return popularity; }
//    public void setPopularity(double popularity) { this.popularity = popularity; }
//    public String getBody() { return body; }
//    public void setSnippet(String snippet) { this.snippet = snippet; }
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getUrl() { return url; }
//    public void setUrl(String url) { this.url = url; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//
//    public String getHeadings() { return headings; }
//    public void setHeadings(String headings) { this.headings = headings; }
//
//    @Override
//    public  String toString() {
//        return "WebDocument{" +
//                ", url='" + url + '\'' +
//                '}';
//    }
//}



package com.example.searchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "documents")
public class WebDocument {
    @Id
    private String id;
    @Indexed
    private String url;
    private String title;
    //    private String body;
    private String headings;

    private String body;
    private double popularity;
    private int wordCount;

    public void setBody(String body) {
        this.body = body;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

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


    public String getHeadings() { return headings; }
    public void setHeadings(String headings) { this.headings = headings; }

    @Override
    public  String toString() {
        return "WebDocument{" +
                ", url='" + url + '\'' +
                '}';
    }
}