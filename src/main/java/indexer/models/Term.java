package indexer.models;

import java.util.ArrayList;
import java.util.List;

public class Term {
    private String term;
    private double tfTitle;
    private double tfHeadings;
    private double tfBody;
    private List<Integer> titlePositions;
    private List<Integer> headingsPositions;
    private List<Integer> bodyPositions;

    public Term(String term) {
        this.term = term;
        this.tfTitle = 0;
        this.tfHeadings = 0;
        this.tfBody = 0;
        this.titlePositions = new ArrayList<>();
        this.headingsPositions = new ArrayList<>();
        this.bodyPositions = new ArrayList<>();
    }

    // Getters
    public String getTerm() { return term; }
    public double getTfTitle() { return tfTitle; }
    public double getTfHeadings() { return tfHeadings; }
    public double getTfBody() { return tfBody; }
    public List<Integer> getTitlePositions() { return titlePositions; }
    public List<Integer> getHeadingsPositions() { return headingsPositions; }
    public List<Integer> getBodyPositions() { return bodyPositions; }

    // Methods to add positions
    public void incrementTfTitle(int position) {
        tfTitle++;
        titlePositions.add(position);
    }

    public void incrementTfHeadings(int position) {
        tfHeadings++;
        headingsPositions.add(position);
    }

    public void incrementTfBody(int position) {
        tfBody++;
        bodyPositions.add(position);
    }
}