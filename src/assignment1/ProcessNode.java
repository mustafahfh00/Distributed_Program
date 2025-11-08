package assignment1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ProcessNode {
    protected final int id;                       // 0..6
    protected final int N = 7;                    // total processes
    protected final int basePort = 6400;
    protected final VectorClock clock = new VectorClock(N);
    protected final Map<Integer, Socket> outgoing = new HashMap<>();
    protected final ServerSocket server;
    protected final Queue<Message> inbox = new ConcurrentLinkedQueue<>();
    protected final List<Message> holdback = new ArrayList<>();
    protected volatile boolean running = true;

    protected ProcessNode(int id) throws IOException {
        this.id = id;
        this.server = new ServerSocket(basePort + id);
    }

    // Connect on demand (lazy)
    protected Socket connectTo(int peerId) throws IOException {
    if (peerId == id) throw new IOException("self-connect");
    if (outgoing.containsKey(peerId) && outgoing.get(peerId).isConnected())
        return outgoing.get(peerId);

    int attempts = 0;
    while (attempts < 10) {
        try {
            Socket s = new Socket("127.0.0.1", basePort + peerId);
            outgoing.put(peerId, s);
            System.out.println("[P" + id + "] Connected to P" + peerId);
            return s;
        } catch (ConnectException e) {
            attempts++;
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
    }
    throw new IOException("Failed to connect to peer " + peerId);
}

    // Send with vector clock tick
    protected void sendMsg(int to, Message.Type type, int wordIndex, String payload) throws IOException {
        clock.tick(id); // local send event tick
        int[] snap = clock.snapshot();
        Message m = new Message(type, id, to, snap, wordIndex, payload);
        Socket s = connectTo(to);
        NetUtil.send(s, m);
    }

    // Background accept loop
    public void startReceiver() {
    Thread t = new Thread(() -> {
        System.out.println("[P" + id + "] receiver thread running...");
        while (running) {
            try {
                Socket s = server.accept();
                // Handle each connection in its own thread
                new Thread(() -> {
                    try {
                        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                        while (running) {
                            Message m = (Message) ois.readObject();
                            if (m == null) break;
                            handleIncoming(m);
                        }
                    } catch (EOFException ignored) {
                        // client closed connection
                    } catch (Exception e) {
                        // log and ignore individual connection errors
                        System.out.println("[P" + id + "] recv error: " + e.getMessage());
                    } finally {
                        try { s.close(); } catch (IOException ignored) {}
                    }
                }, "conn-" + id).start();
            } catch (IOException e) {
                if (running) System.out.println("[P" + id + "] accept failed: " + e.getMessage());
            }
        }
    }, "recv-" + id);
    t.setDaemon(true);
    t.start();
}


    // Causal delivery with hold-back
    protected synchronized void handleIncoming(Message m) {
        holdback.add(m);
        tryDeliver();
    }

    protected synchronized void tryDeliver() {
        boolean progressed;
        do {
            progressed = false;
            Iterator<Message> it = holdback.iterator();
            while (it.hasNext()) {
                Message m = it.next();
                if (clock.canDeliver(m.fromId, m.vc)) {
                    // Merge + tick receive
                    clock.receiveFrom(id, m.vc);
                    inbox.add(m);
                    it.remove();
                    progressed = true;
                }
            }
        } while (progressed);
    }

    // Application-specific behavior
    protected abstract void onDeliver(Message m) throws Exception;

    // Main loop to consume delivered messages
    public void runLoop() {
        startReceiver();
        while (running) {
            Message m = inbox.poll();
            if (m != null) {
                try { onDeliver(m); } catch (Exception e) { e.printStackTrace(); }
            } else {
                try { Thread.sleep(5); } catch (InterruptedException ignored) {}
            }
        }
        try { server.close(); } catch (IOException ignored) {}
    }
}
