package indexer.models;

public class DocumentTermInfo {
    private double tfTitle;
    private double tfHeadings;
    private double tfBody;

    public DocumentTermInfo(double tfTitle, double tfHeadings, double tfBody) {
        this.tfTitle = tfTitle;
        this.tfHeadings = tfHeadings;
        this.tfBody = tfBody;
    }

    // Getters
    public double getTfTitle() { return tfTitle; }
    public double getTfHeadings() { return tfHeadings; }
    public double getTfBody() { return tfBody; }
}
