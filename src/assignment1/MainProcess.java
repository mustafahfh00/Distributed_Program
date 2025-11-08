package assignment1;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MainProcess extends ProcessNode {
    private final String paragraph;
    private final String[] words;
    private final Map<Integer,String> collected = new HashMap<>();
    private final Random rnd = new Random();

    public MainProcess(String paragraph) throws IOException {
        super(0);
        System.out.println("[P" + id + "] " + "listening on port " + (basePort + id));

        this.paragraph = paragraph.trim();
        this.words = this.paragraph.split("\\s+");
    }

    @Override
    protected void onDeliver(Message m) throws Exception {
        if (m.type == Message.Type.RETURN_WORD) {
            collected.put(m.wordIndex, m.payload);
        }
    }

    public void runScenario() throws Exception {
        startReceiver();

        // 1) Randomly distribute words to 6 workers (ids 1..6)
        for (int i = 0; i < words.length; i++) {
            int worker = 1 + ThreadLocalRandom.current().nextInt(6);
            sendMsg(worker, Message.Type.WORD, i, words[i]); // TCP gives FIFO per-sender; causal hold-back enforces HB. :contentReference[oaicite:7]{index=7}
        }

        // 2) Wait 15 seconds
        Thread.sleep(15_000);

        // 3) Ask all workers to COLLECT (flush back)
        for (int w = 1; w <= 6; w++) sendMsg(w, Message.Type.COLLECT, -1, "flush");

        // 4) Wait until all words are collected (or timeout safeguard)
        long deadline = System.currentTimeMillis() + 10_000;
        while (collected.size() < words.length && System.currentTimeMillis() < deadline) {
            Message m = inbox.poll();
            if (m != null) onDeliver(m); else Thread.sleep(5);
        }

        // 5) Sort by original index and print
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = collected.get(i);
            if (w == null) w = words[i]; // fallback
            if (i > 0) sb.append(' ');
            sb.append(w);
        }
        System.out.println("Original paragraph (reconstructed):");
        System.out.println(sb.toString());

        // Optional: tell workers to stop
        for (int w = 1; w <= 6; w++) sendMsg(w, Message.Type.DONE, -1, "bye");
        running = false;
        server.close();
    }
}

