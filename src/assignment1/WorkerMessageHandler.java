package assignment1;

import java.util.Map;

/**
 * WorkerMessageHandler
 * ------------------------------------------------------------
 * Encapsulates logic for:
 *  - handling DATA messages (store words)
 *  - handling COLLECT messages (send stored words to Main)
 *
 * This keeps WorkerProcess clean and readable.
 *
 * References:
 *  - SE355 Notebook Lectures 11–17 (Causality & Message Delivery)
 *  - Kshemkalyani & Singhal, Ch. 6 (Message Ordering Policies)
 */
public class WorkerMessageHandler {

    private final int workerId;
    private final VectorClock clock;
    private final Map<Integer, String> wordStore;

    public WorkerMessageHandler(int workerId, VectorClock clock, Map<Integer, String> wordStore) {
        this.workerId = workerId;
        this.clock = clock;
        this.wordStore = wordStore;
    }

    /** Handle DATA message → store word & index. */
    public void handleDataMessage(Message msg) {
        wordStore.put(msg.getIndex(), msg.getContent());
    }

    /** Handle COLLECT → send all stored words back to Main (P0). */
    public void handleCollectMessage() {
        for (var entry : wordStore.entrySet()) {
            Message response = new Message(
                    Message.Type.RESPONSE,
                    entry.getValue(),
                    workerId,
                    clock.copy(),
                    entry.getKey()
            );

            try {
                NetUtil.send("localhost", 5000, response);
            } catch (Exception e) {
                System.out.println("[P" + workerId + "] ⚠ Unable to send index "
                        + entry.getKey() + " back to Main");
            }
        }
        wordStore.clear();
    }
}
