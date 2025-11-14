package assignment1;

import java.util.Map;
import java.util.TreeMap;

/**
 * WordCollector
 * ------------------------------------------------------------
 * Stores (index → word) pairs received from workers and
 * reconstructs them in original order.
 *
 * References:
 *  - SE355 Notebook Lecture 11 (Partial Order)
 *  - Lecture 7 (I/O & message receiving)
 */
public class WordCollector {

    // Sorted map so words reconstruct in correct order
    private final Map<Integer, String> collected = new TreeMap<>();

    /** Store a received word. */
    public void store(int index, String word) {
        collected.put(index, word);
    }

    /** Access the internal TreeMap. */
    public Map<Integer, String> getAll() {
        return collected;
    }

    /** Clear after use if needed. */
    public void clear() {
        collected.clear();
    }
}
