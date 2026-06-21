package main;

import network.Command;
import network.Response;
import network.ServerConnection;
import network.dto.LoginDTO;

public class ConcurrencyTest {
    private static final int CLIENT_COUNT = 5;

    public static void main(String[] args) throws InterruptedException {
        Thread[] clients = new Thread[CLIENT_COUNT];

        for (int i = 0; i < CLIENT_COUNT; i++) {
            final int clientId = i;
            clients[i] = new Thread(() -> {
                try (ServerConnection conn = new ServerConnection("localhost", 8080)) {
                    Response ping = conn.send(Command.PING, null);
                    Response login = conn.send(Command.LOGIN,
                            new LoginDTO("sub_285107", "secret123"));
                    System.out.println("Client " + clientId + " | PING=" + ping
                            + " | LOGIN=" + login);
                } catch (Exception e) {
                    System.err.println("Client " + clientId + " failed: " + e.getMessage());
                }
            }, "Client-" + i);
        }

        for (Thread t : clients) t.start();
        for (Thread t : clients) t.join();

        System.out.println("All " + CLIENT_COUNT + " clients finished.");
    }
}
