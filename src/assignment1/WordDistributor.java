package assignment1;

import java.util.List;
import java.util.Random;

/**
 * WordDistributor
 * ------------------------------------------------------------
 * Responsible for:
 *  - splitting input paragraph into words
 *  - choosing random workers for each word
 *
 * References:
 *  - SE355 Notebook Lecture 12 (Event Modeling, Local State)
 *  - Java String utilities (covered in class)
 */
public class WordDistributor {

    private final Random random = new Random();

    /** Split a paragraph into words by whitespace. */
    public String[] extractWords(String paragraph) {
        return paragraph.trim().split("\\s+");
    }

    /**
     * Randomly choose a worker ID between 1–6 for each word.
     */
    public int chooseRandomWorker() {
        return 1 + random.nextInt(6);  // workers P1–P6
    }
}
