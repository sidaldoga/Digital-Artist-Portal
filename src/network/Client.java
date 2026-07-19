package network;

import network.dto.LoginDTO;
import java.io.IOException;

public class Client implements AutoCloseable {
    private final ServerConnection conn;

    public Client(String host, int port) throws IOException {
        this.conn = new ServerConnection(host, port);
    }
    public Response login(String username, String password) throws Exception {
        return conn.send(Command.LOGIN, new LoginDTO(username, password));
    }

    public Response getAllAddresses() throws Exception {
        return conn.send(Command.GET_ALL_ADDRESSES, null);
    }

    public Response getAddress(int id) throws Exception {
        return conn.send(Command.GET_ADDRESS, id);
    }

    public Response ping() throws Exception {
        return conn.send(Command.PING, null);
    }

    public void close() throws Exception {
        conn.send(Command.BYE, null);
        conn.close();
    }
}