package assignment1;

import java.net.ServerSocket;

/**
 * Base abstract class for all processes (Main and Workers).
 * Each process has:
 *  - an ID (0–6)
 *  - a port (5000 + id)
 *  - a vector clock
 *
 * References:
 *  - SE355 Notebook (Process Model, Local State)
 *  - Java Network Programming (Socket lifecycle)
 */
public abstract class ProcessNode {

    protected final int id;
    protected final int port;
    protected final VectorClock clock;

    public ProcessNode(int id, int totalProcesses) {
        this.id = id;
        this.port = 5000 + id;
        this.clock = new VectorClock(totalProcesses);
    }

    /** Open a ServerSocket for this process. */
    protected ServerSocket openServer() throws Exception {
        return new ServerSocket(port);
    }

    /** Increment logical clock (local event). */
    protected void incrementClock() {
        clock.tick(id);
    }

    /** Log with vector clock included. */
    protected void log(String msg) {
        System.out.printf("[P%d %s] %s%n", id, clock, msg);
    }
}
