package assignment1;

import java.net.ServerSocket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * WorkerProcess (P1–P6)
 * ------------------------------------------------------------
 * Responsibilities:
 *  - Receive DATA messages → store words with index
 *  - Receive COLLECT message → send all stored words to Main (P0)
 *  - Maintain vector-clock causal ordering
 *
 * Clean Architecture:
 *  - WorkerMessageHandler handles DATA / COLLECT logic
 *  - WorkerProcess handles networking + clock updates
 *
 * References:
 *  - SE355 Notebook Lec. 11–17 (Causality, Vector Time, Delivery Rules)
 *  - Kshemkalyani & Singhal, Ch. 3 & 6
 *  - Java Network Programming (Sockets + Object Streams)
 */
public class WorkerProcess extends ProcessNode {

    // Stores words sorted by their original index
    private final Map<Integer, String> wordStore =
            Collections.synchronizedMap(new TreeMap<>());

    private final WorkerMessageHandler handler;

    public WorkerProcess(int id, int totalProcesses) {
        super(id, totalProcesses);
        this.handler = new WorkerMessageHandler(id, clock, wordStore);
    }

    /** Main server loop for this worker. */
    public void startWorker() throws Exception {
        try (ServerSocket server = openServer()) {

            log("listening on port " + port);

            while (true) {
                Message msg = NetUtil.receive(server);

                // Causality update
                clock.update(msg.getVectorClock());
                incrementClock();

                switch (msg.getType()) {

                    case DATA -> {
                        handler.handleDataMessage(msg);
                        log("stored '" + msg.getContent() + "' (index " + msg.getIndex() + ")");
                    }

                    case COLLECT -> {
                        handler.handleCollectMessage();
                        log("sent back all stored words to Main");
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java assignment1.WorkerProcess <id>");
            return;
        }
        int id = Integer.parseInt(args[0]);
        new WorkerProcess(id, 7).startWorker();
    }
}
