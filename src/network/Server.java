package network;

import db.dao.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 8080;

        UserDAO userDAO = new UserDAO();
        ArtProductDAO artProductDAO = new ArtProductDAO(userDAO);
        AddressDAO addressDAO = new AddressDAO();
        ArtistDAO artistDAO = new ArtistDAO(userDAO);
        DigitalProductDAO digitalProductDAO = new DigitalProductDAO(artProductDAO);
        DimensionsDAO dimensionsDAO = new DimensionsDAO();
        PhysicalProductDAO physicalProductDAO = new PhysicalProductDAO(artProductDAO,dimensionsDAO);
        SubscriberDAO subscriberDAO = new SubscriberDAO(userDAO);
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(userDAO);
        PurchaseDAO purchaseDAO = new PurchaseDAO();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, waiting for connections on port 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new user has connected.");


                Thread t = new Thread(new ClientHandler(
                        clientSocket,
                        userDAO,
                        artProductDAO,
                        addressDAO,
                        artistDAO,
                        digitalProductDAO,
                        dimensionsDAO,
                        physicalProductDAO,
                        subscriberDAO,
                        subscriptionDAO,
                        purchaseDAO
                ));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}