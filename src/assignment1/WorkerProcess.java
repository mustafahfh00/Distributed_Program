package assignment1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkerProcess extends ProcessNode {
    private final List<Message> stored = new ArrayList<>();

    public WorkerProcess(int id) throws IOException { super(id);
    System.out.println("[P" + id + "] " + "listening on port " + (basePort + id));
 }

    @Override
    protected void onDeliver(Message m) throws Exception {
        switch (m.type) {
            case WORD:
                stored.add(m);
                break;
            case COLLECT:
                // Send all stored words back to Main (id 0)
                for (Message w : stored) {
                    sendMsg(0, Message.Type.RETURN_WORD, w.wordIndex, w.payload);
                }
                stored.clear();
                break;
            case DONE:
                running = false; // optional shutdown
                break;
            default:
                break;
        }
    }
}

