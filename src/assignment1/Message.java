package assignment1;

import java.io.Serializable;
import java.util.Arrays;

/**
 * SE355 – Distributed Systems
 * Assignment 1
 *
 * Serializable message exchanged between processes.
 * Each message carries:
 *  - type: DATA, COLLECT, RESPONSE
 *  - content: the word or payload
 *  - fromId: sender process ID
 *  - vectorClock: attached logical clock (causal ordering)
 *  - index: original word index for reconstruction
 *
 * References:
 *  - SE355 Notebook: Lectures 11–17 (Causality, Lamport & Vector Clocks)
 *  - Kshemkalyani & Singhal, Chapter 3 (Logical Time)
 *  - Java Network Programming, Ch. 8–9 (Object Streams)
 */
public class Message implements Serializable {

    public enum Type { DATA, COLLECT, RESPONSE }

    private final Type type;
    private final String content;
    private final int fromId;
    private final int[] vectorClock;
    private final int index;

    public Message(Type type, String content, int fromId, int[] vectorClock, int index) {
        this.type = type;
        this.content = content;
        this.fromId = fromId;
        this.vectorClock = vectorClock;
        this.index = index;
    }

    public Type getType() { return type; }
    public String getContent() { return content; }
    public int getFromId() { return fromId; }
    public int[] getVectorClock() { return vectorClock; }
    public int getIndex() { return index; }

    @Override
    public String toString() {
        return "[" + type +
                " from=P" + fromId +
                ", index=" + index +
                ", vc=" + Arrays.toString(vectorClock) +
                ", content=" + content + "]";
    }
}
