package assignment1;

/**
 * VectorClock implementation for causal ordering.
 *
 * Each process maintains an array of integers, one entry per process.
 * Rules followed are from SE355 Lecture 17:
 *  - tick() increments local component
 *  - update(receiver) merges received vector
 *
 * References:
 *  - SE355 Notebook Lecture 17 (Vector Clocks)
 *  - Kshemkalyani & Singhal, §3.4 (Vector Timestamp Rules)
 */
public class VectorClock {
    private final int[] clock;

    public VectorClock(int totalProcesses) {
        this.clock = new int[totalProcesses];
    }

    /** Local event → increment own clock entry. */
    public synchronized void tick(int pid) {
        clock[pid]++;
    }

    /** Merge received vector clock (element-wise max). */
    public synchronized void update(int[] other) {
        for (int i = 0; i < clock.length; i++) {
            clock[i] = Math.max(clock[i], other[i]);
        }
    }

    /** Return a deep copy. */
    public synchronized int[] copy() {
        return clock.clone();
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < clock.length; i++) {
            sb.append(clock[i]);
            if (i < clock.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
