package indexer.models;

import java.util.ArrayList;
import java.util.List;

public class DocumentTermInfo {
    private double tfTitle;
    private double tfHeadings;
    private double tfBody;
    private List<Integer> titlePositions;
    private List<Integer> headingsPositions;
    private List<Integer> bodyPositions;

    public DocumentTermInfo(double tfTitle, double tfHeadings, double tfBody,
                            List<Integer> titlePositions, List<Integer> headingsPositions, List<Integer> bodyPositions) {
        this.tfTitle = tfTitle;
        this.tfHeadings = tfHeadings;
        this.tfBody = tfBody;
        this.titlePositions = titlePositions != null ? titlePositions : new ArrayList<>();
        this.headingsPositions = headingsPositions != null ? headingsPositions : new ArrayList<>();
        this.bodyPositions = bodyPositions != null ? bodyPositions : new ArrayList<>();
    }

    // Getters
    public double getTfTitle() { return tfTitle; }
    public double getTfHeadings() { return tfHeadings; }
    public double getTfBody() { return tfBody; }
    public List<Integer> getTitlePositions() { return titlePositions; }
    public List<Integer> getHeadingsPositions() { return headingsPositions; }
    public List<Integer> getBodyPositions() { return bodyPositions; }
}
