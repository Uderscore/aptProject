package com.example.searchengine.model;

import java.util.List;

public class DocumentTermInfo {
    private String url;
    private double tfTitle;
    private double tfHeadings;
    private double tfBody;
    private List<Integer> titlePositions;
    private List<Integer> headingsPositions;
    private List<Integer> bodyPositions;

    // Getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public double getTfTitle() { return tfTitle; }
    public void setTfTitle(double tfTitle) { this.tfTitle = tfTitle; }
    
    public double getTfHeadings() { return tfHeadings; }
    public void setTfHeadings(double tfHeadings) { this.tfHeadings = tfHeadings; }
    
    public double getTfBody() { return tfBody; }
    public void setTfBody(double tfBody) { this.tfBody = tfBody; }
    
    public List<Integer> getTitlePositions() { return titlePositions; }
    public void setTitlePositions(List<Integer> titlePositions) { this.titlePositions = titlePositions; }
    
    public List<Integer> getHeadingsPositions() { return headingsPositions; }
    public void setHeadingsPositions(List<Integer> headingsPositions) { this.headingsPositions = headingsPositions; }
    
    public List<Integer> getBodyPositions() { return bodyPositions; }
    public void setBodyPositions(List<Integer> bodyPositions) { this.bodyPositions = bodyPositions; }

    @Override
    public String toString() {
        return "DocumentTermInfo{" +
                "url='" + url + '\'' +
                '}';
    }
}
