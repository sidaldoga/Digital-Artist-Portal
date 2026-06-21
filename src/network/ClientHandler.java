package network;

import network.dto.LoginDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            LoginDTO receivedLogin = (LoginDTO) in.readObject();

            System.out.println("Communication channels with the customer have been opened.");

        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
