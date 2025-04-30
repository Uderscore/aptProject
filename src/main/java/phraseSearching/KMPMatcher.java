package phraseSearching;

import java.util.List;

public class KMPMatcher {
    private int[] lps;
    private List<String> pattern;

    public KMPMatcher(List<String> pattern) {
        this.pattern = pattern;
        this.lps = new int[pattern.size()];
        buildLps();
    }

    private void buildLps() {
        int j = 0;
        lps[0] = 0;
        for (int i = 1; i < pattern.size(); i++) {
            while (j > 0 && !pattern.get(i).equals(pattern.get(j))) {
                j = lps[j - 1];
            }
            if (pattern.get(i).equals(pattern.get(j))) {
                j++;
            }
            lps[i] = j;
        }
    }

    public boolean matches(List<String> text) {
        int j = 0;
        for (int i = 0; i < text.size(); i++) {
            while (j > 0 && !text.get(i).equals(pattern.get(j))) {
                j = lps[j - 1];
            }
            if (text.get(i).equals(pattern.get(j))) {
                j++;
            }
            if (j == pattern.size())
                return true;
        }
        return false;
    }
}
