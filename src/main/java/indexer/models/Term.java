package indexer.models;

public class Term {
    private String term;
    private double tfTitle;
    private double tfHeadings;
    private double tfBody;

    public Term(String term) {
        this.term = term;
        this.tfTitle = 0;
        this.tfHeadings = 0;
        this.tfBody = 0;
    }

    // Getters and increment methods
    public String getTerm() { return term; }
    public double getTfTitle() { return tfTitle; }
    public double getTfHeadings() { return tfHeadings; }
    public double getTfBody() { return tfBody; }

    public void incrementTfTitle() { tfTitle++; }
    public void incrementTfHeadings() { tfHeadings++; }
    public void incrementTfBody() { tfBody++; }
}