package assignment1;

import java.io.*;
import java.net.*;

/**
 * Utility class for sending/receiving Message objects across sockets.
 * Implements simple retry logic for ConnectionRefused.
 *
 * References:
 *  - Java Network Programming (Harold), Ch. 2–5
 *  - SE355 Notebook Lecture 5–7 (Client/Server, Streams, Serialization)
 */
public class NetUtil {

    /**
     * Send a Message to host:port with 3 retry attempts.
     */
    public static void send(String host, int port, Message msg) throws IOException {
        int attempts = 0;

        while (attempts < 3) {
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out =
                         new ObjectOutputStream(socket.getOutputStream())) {

                out.writeObject(msg);
                out.flush();
                return;
            }
            catch (ConnectException e) {
                attempts++;
                System.out.println("[NetUtil] Connection refused (attempt "
                                   + attempts + "), retrying...");
                sleep(1000);

                if (attempts == 3) throw e;
            }
        }
    }

    /**
     * Receive a Message through a ServerSocket.
     * Blocking call until a connection is accepted.
     */
    public static Message receive(ServerSocket server)
            throws IOException, ClassNotFoundException {

        try (Socket socket = server.accept();
             ObjectInputStream in =
                     new ObjectInputStream(socket.getInputStream())) {
            return (Message) in.readObject();
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
