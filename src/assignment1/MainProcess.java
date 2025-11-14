package assignment1;

import java.net.ServerSocket;
import java.util.Map;
import java.util.Scanner;

/**
 * MainProcess (P0)
 * ------------------------------------------------------------
 * Responsibilities:
 *  - Prompt user for a paragraph
 *  - Distribute each word to a random worker (P1–P6)
 *  - Wait 15 seconds
 *  - Collect all returned words
 *  - Reconstruct the original paragraph in correct order
 *
 * Clean Architecture:
 *  - WordDistributor handles splitting + randomness
 *  - WordCollector stores sorted results
 *  - MainProcess only coordinates high-level behavior
 *
 * References:
 *  - SE355 Notebook Lectures 11–17 (Causality, Vector Clocks)
 *  - Kshemkalyani & Singhal, Ch. 3 (Vector Time)
 *  - Java Network Programming (Harold), Ch. 8–9 (Object Streams)
 */
public class MainProcess extends ProcessNode {

    private final WordDistributor distributor = new WordDistributor();
    private final WordCollector collector = new WordCollector();

    public MainProcess() {
        super(0, 7); // Main is P0 in a 7-process system
    }

    /** Entry point: coordinates the entire workflow. */
    public void run() throws Exception {
        String paragraph = promptUserForParagraph();
        String[] words = distributor.extractWords(paragraph);

        distributeWords(words);

        waitBeforeCollecting();

        sendCollectRequests();

        collectResponses();

        printReconstructedParagraph();
    }

    // ------------------------------------------------------------
    // 1. USER INPUT
    // ------------------------------------------------------------
    private String promptUserForParagraph() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a paragraph: ");
        return sc.nextLine();
    }

    // ------------------------------------------------------------
    // 2. DISTRIBUTION PHASE
    // ------------------------------------------------------------
    private void distributeWords(String[] words) throws Exception {
        for (int index = 0; index < words.length; index++) {
            String word = words[index];
            int workerId = distributor.chooseRandomWorker();

            incrementClock(); // local event
            Message msg = new Message(
                    Message.Type.DATA,
                    word,
                    id,
                    clock.copy(),
                    index
            );

            NetUtil.send("localhost", 5000 + workerId, msg);

            log("sent '" + word + "' (index " + index + ") to P" + workerId);
        }
    }

    // ------------------------------------------------------------
    // 3. DELAY PHASE
    // ------------------------------------------------------------
    private void waitBeforeCollecting() {
        System.out.println("\n⏳ Waiting 15 seconds before collection...");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException ignored) {}
    }

    // ------------------------------------------------------------
    // 4. COLLECT REQUESTS
    // ------------------------------------------------------------
    private void sendCollectRequests() throws Exception {
        for (int workerId = 1; workerId <= 6; workerId++) {
            incrementClock();

            Message request = new Message(
                    Message.Type.COLLECT,
                    "",
                    id,
                    clock.copy(),
                    -1
            );

            NetUtil.send("localhost", 5000 + workerId, request);

            log("sent COLLECT to P" + workerId);
        }
    }

    // ------------------------------------------------------------
    // 5. RECEIVE RESPONSES
    // ------------------------------------------------------------
    private void collectResponses() throws Exception {
        try (ServerSocket server = openServer()) {
            server.setSoTimeout(12000);  // max wait 12 seconds
            long endTime = System.currentTimeMillis() + 12000;

            while (System.currentTimeMillis() < endTime) {
                try {
                    Message resp = NetUtil.receive(server);

                    // merge vector clock
                    clock.update(resp.getVectorClock());
                    incrementClock();

                    collector.store(resp.getIndex(), resp.getContent());

                    log("received '" + resp.getContent() +
                        "' (index " + resp.getIndex() +
                        ") from P" + resp.getFromId());

                } catch (Exception ignored) {
                    // timeout or worker not ready → skip and continue
                }
            }
        }
    }

    // ------------------------------------------------------------
    // 6. OUTPUT
    // ------------------------------------------------------------
    private void printReconstructedParagraph() {
        System.out.println("\n🧾 Reconstructed paragraph:");

        Map<Integer, String> received = collector.getAll();

        if (received.isEmpty()) {
            System.out.println("(No words received — check timing or ports)");
            return;
        }

        for (String word : received.values()) {
            System.out.print(word + " ");
        }
        System.out.println();
    }

    // ------------------------------------------------------------
    // MAIN ENTRY
    // ------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        new MainProcess().run();
    }
}
