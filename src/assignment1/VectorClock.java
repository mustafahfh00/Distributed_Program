package assignment1;

import java.util.Arrays;

public class VectorClock {
    private final int[] v;

    public VectorClock(int size) { this.v = new int[size]; }
    public synchronized int[] snapshot() { return Arrays.copyOf(v, v.length); }

    // Local event tick
    public synchronized void tick(int pid) { v[pid]++; }

    // On receive: merge then tick
    public synchronized void receiveFrom(int pid, int[] other) {
        for (int i = 0; i < v.length; i++) v[i] = Math.max(v[i], other[i]);
        v[pid]++; // count the receive event at this process
    }

    // Causal deliverability test for a msg from sender s with clock m
    public synchronized boolean canDeliver(int sender, int[] m) {
        if (m[sender] != v[sender] + 1) return false;
        for (int i = 0; i < v.length; i++) if (i != sender && m[i] > v[i]) return false;
        return true;
    }

    @Override public synchronized String toString() { return Arrays.toString(v); }
}
