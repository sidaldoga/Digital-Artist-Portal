package network;

import network.dto.LoginDTO;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        try (ServerConnection conn = new ServerConnection(host, port)) {
            System.out.println("Connected to the server at " + host + ":" + port);

            System.out.println("PING-> " + conn.send(Command.PING, null));

            Response login = conn.send(Command.LOGIN, new LoginDTO("artbysidal", "KatzeHund2001"));
            System.out.println("LOGIN-> " + login);

            Response addresses = conn.send(Command.GET_ALL_ADDRESSES, null);
            System.out.println("GET_ALL_ADDRESSES -> " + addresses.getStatus()
                    + " (" + payloadSize(addresses) + " addresses)");

            System.out.println("GET_ADDRESS 1-> " + conn.send(Command.GET_ADDRESS, 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected.");
    }

    private static int payloadSize(Response r) {
        return (r.getPayload() instanceof java.util.List<?> list) ? list.size() : 0;
    }
}
