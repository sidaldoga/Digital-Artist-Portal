package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public Response send(Command command, Object data) throws IOException, ClassNotFoundException {
        out.writeObject(new Request(command, data));
        out.flush();
        out.reset();
        return (Response) in.readObject();
    }

    @Override
    public void close() throws IOException {
        try {
            out.writeObject(new Request(Command.BYE, null));
            out.flush();
        } catch (IOException ignored) {
        } finally {
            socket.close();
        }
    }
}
