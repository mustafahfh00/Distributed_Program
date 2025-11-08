package assignment1;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class NetUtil {
    private static final ConcurrentHashMap<Socket,ObjectOutputStream> map = new ConcurrentHashMap<>();

    public static synchronized void send(Socket s, Message m) throws IOException {
        ObjectOutputStream oos = map.computeIfAbsent(s, sock -> {
            try { return new ObjectOutputStream(sock.getOutputStream()); }
            catch (IOException e) { throw new RuntimeException(e); }
        });
        oos.writeObject(m);
        oos.flush();
    }


    public static Message recv(Socket s) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        return (Message) ois.readObject();
    }
}
