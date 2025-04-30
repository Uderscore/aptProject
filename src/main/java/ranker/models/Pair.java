package ranker.models;

import java.util.Objects;

/**
 * A generic pair class that holds two values of potentially different types.
 * This class is immutable and thread-safe.
 *
 * @param <K> Type of the first element
 * @param <V> Type of the second element
 */
public class Pair<K, V> implements Comparable<Pair<K, V>> {
    private final K first;
    private final V second;

    /**
     * Creates a new pair with the given values.
     *
     * @param first  The first value
     * @param second The second value
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first value of this pair.
     *
     * @return The first value
     */
    public K getFirst() {
        return first;
    }

    /**
     * Gets the second value of this pair.
     *
     * @return The second value
     */
    public V getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Compares this pair with another pair.
     * This implementation requires both elements to implement Comparable.
     *
     * @param other The pair to compare with
     * @return A negative integer, zero, or a positive integer if this pair is less than, equal to, or greater than the other pair
     * @throws ClassCastException If the elements cannot be cast to Comparable
     */
    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Pair<K, V> other) {
        if (first instanceof Comparable && other.first instanceof Comparable) {
            int result = ((Comparable<K>) first).compareTo(other.first);
            if (result != 0) return result;
        }

        if (second instanceof Comparable && other.second instanceof Comparable) {
            return ((Comparable<V>) second).compareTo(other.second);
        }

        return 0;
    }

    /**
     * Creates a new pair with the given values.
     *
     * @param <K>    Type of the first element
     * @param <V>    Type of the second element
     * @param first  The first value
     * @param second The second value
     * @return A new pair containing the values
     */
    public static <K, V> Pair<K, V> of(K first, V second) {
        return new Pair<>(first, second);
    }
}