package network;

import network.dto.LoginDTO;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("I connected to the server.");

            LoginDTO loginRequest = new LoginDTO("loginName", "password123");
            out.writeObject(loginRequest);
            out.flush();

            System.out.println("Login details have been sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
