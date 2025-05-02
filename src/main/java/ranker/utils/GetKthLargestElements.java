package ranker.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import ranker.models.Pair;

public class GetKthLargestElements {
    private static final Random random = new Random();

    public static List<String> getNthElements(List<Pair<String, Double>> pairs, int topK) {
        if (pairs == null || pairs.isEmpty() || topK <= 0 || topK > pairs.size()) {
            return new ArrayList<>();
        }

        // Convert to array for easier manipulation
        Pair<String, Double>[] array = pairs.toArray(new Pair[0]);

        // We need to find the (n - topK)th element (0-based)
        int k = array.length - topK;

        int low = 0;
        int high = array.length - 1;

        while (low < high) {
            int rand = getRandomNumber(low, high);
            swap(array, rand, low);
            int p = partition(array, low, high);

            if (p < k) {
                low = p + 1;
            } else {
                high = p;
            }
        }

        // Now collect all elements from the k-th position to the end
        List<String> result = new ArrayList<>();
        for (int i = k; i < array.length; i++) {
            result.add(array[i].getFirst());
        }

        return result;
    }

    private static int partition(Pair<String, Double>[] array, int low, int high) {
        double pivot = array[low].getSecond();
        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (i <= high && array[i].getSecond() < pivot);

            do {
                j--;
            } while (j >= low && array[j].getSecond() > pivot);

            if (i >= j) {
                return j;
            }

            swap(array, i, j);
        }
    }

    private static void swap(Pair<String, Double>[] array, int i, int j) {
        Pair<String, Double> temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static int getRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}