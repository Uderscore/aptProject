package com.example.searchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "terms")
public class InvertedTerm {
    @Id
    private String id;

    private String term;
    List<Posting> documents;
    Integer df;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<Posting> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Posting> documents) {
        this.documents = documents;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }

    public static class Posting {
        String url;
        int tf_title;
        int tf_headings;
        int tf_body;
        List<Integer> title_positions;
        List<Integer> headings_positions;
        List<Integer> body_positions;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getTf_title() {
            return tf_title;
        }

        public void setTf_title(int tf_title) {
            this.tf_title = tf_title;
        }

        public int getTf_headings() {
            return tf_headings;
        }

        public void setTf_headings(int tf_headings) {
            this.tf_headings = tf_headings;
        }

        public int getTf_body() {
            return tf_body;
        }

        public void setTf_body(int tf_body) {
            this.tf_body = tf_body;
        }

        public List<Integer> getTitle_positions() {
            return title_positions;
        }

        public void setTitle_positions(List<Integer> title_positions) {
            this.title_positions = title_positions;
        }

        public List<Integer> getHeadings_positions() {
            return headings_positions;
        }

        public void setHeadings_positions(List<Integer> headings_positions) {
            this.headings_positions = headings_positions;
        }

        public List<Integer> getBody_positions() {
            return body_positions;
        }

        public void setBody_positions(List<Integer> body_positions) {
            this.body_positions = body_positions;
        }
    }

}
