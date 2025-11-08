package assignment1;


import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    public enum Type { WORD, DONE, COLLECT, RETURN_WORD }
    public final Type type;
    public final int fromId;
    public final int toId;
    public final int[] vc;           // vector clock snapshot
    public final int wordIndex;      // original index in input
    public final String payload;     // the word itself (or control text)

    public Message(Type type, int fromId, int toId, int[] vc, int wordIndex, String payload) {
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
        this.vc = vc;
        this.wordIndex = wordIndex;
        this.payload = payload;
    }

    @Override public String toString() {
        return "Message{" + type + ", from=" + fromId + ", to=" + toId +
                ", idx=" + wordIndex + ", vc=" + Arrays.toString(vc) +
                ", payload='" + payload + "'}";
    }
}
