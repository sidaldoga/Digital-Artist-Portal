package network;

import network.dto.DigitalProductDTO;
import network.dto.LoginDTO;
import network.dto.PhysicalProductDTO;
import network.dto.PurchaseDTO;
import network.dto.SubscriptionDTO;
import network.dto.UserDTO;

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

    public Response register(UserDTO dto) throws Exception {
        return conn.send(Command.REGISTER, dto);
    }

    public Response getAllArtists() throws Exception {
        return conn.send(Command.GET_ALL_ARTISTS, null);
    }

    public Response getAllArtProducts() throws Exception {
        return conn.send(Command.GET_ALL_ART_PRODUCTS, null);
    }

    public Response getAllSubscriptions() throws Exception {
        return conn.send(Command.GET_ALL_SUBSCRIPTIONS, null);
    }

    public Response addSubscription(SubscriptionDTO sub) throws Exception {
        return conn.send(Command.ADD_SUBSCRIPTION, sub);
    }

    public Response addPhysicalProduct(PhysicalProductDTO dto) throws Exception {
        return conn.send(Command.ADD_PHYSICAL_PRODUCT, dto);
    }

    public Response addDigitalProduct(DigitalProductDTO dto) throws Exception {
        return conn.send(Command.ADD_DIGITAL_PRODUCT, dto);
    }

    public Response getAllPhysicalProducts() throws Exception {
        return conn.send(Command.GET_ALL_PHYSICAL_PRODUCTS, null);
    }

    public Response getAllDigitalProducts() throws Exception {
        return conn.send(Command.GET_ALL_DIGITAL_PRODUCTS, null);
    }

    public Response addPurchase(PurchaseDTO dto) throws Exception {
        return conn.send(Command.ADD_PURCHASE, dto);
    }

    public Response removeSubscription(int subscriberId, int artistId) throws Exception {
        return conn.send(Command.REMOVE_SUBSCRIPTION,
                new SubscriptionDTO(0, subscriberId, "", artistId, "", false));
    }

    public Response getSalesLogsByArtist(int artistId) throws Exception {
        return conn.send(Command.GET_SALES_LOGS_BY_ARTIST, artistId);
    }
}