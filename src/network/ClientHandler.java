package network;

import db.DatabaseLock;
import db.dao.*;
import model.*;
import network.dto.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserDAO userDAO;
    private final ArtProductDAO artProductDAO;
    private final AddressDAO addressDAO;
    private final ArtistDAO artistDAO;
    private final DigitalProductDAO digitalProductDAO;
    private final DimensionsDAO dimensionsDAO;
    private final PhysicalProductDAO physicalProductDAO;
    private final SubscriberDAO subscriberDAO;
    private final SubscriptionDAO subscriptionDAO;
    private final PurchaseDAO purchaseDAO;

    public ClientHandler(Socket socket, UserDAO userDAO, ArtProductDAO artProductDAO, AddressDAO addressDAO,
                         ArtistDAO artistDAO, DigitalProductDAO digitalProductDAO, DimensionsDAO dimensionsDAO,
                         PhysicalProductDAO physicalProductDAO, SubscriberDAO subscriberDAO,
                         SubscriptionDAO subscriptionDAO, PurchaseDAO purchaseDAO) {
        this.socket = socket;
        this.userDAO = userDAO;
        this.artProductDAO = artProductDAO;
        this.addressDAO = addressDAO;
        this.artistDAO = artistDAO;
        this.digitalProductDAO = digitalProductDAO;
        this.dimensionsDAO = dimensionsDAO;
        this.physicalProductDAO = physicalProductDAO;
        this.subscriberDAO = subscriberDAO;
        this.subscriptionDAO = subscriptionDAO;
        this.purchaseDAO = purchaseDAO;
    }

    @Override
    public void run() {

        try (Socket s = this.socket;
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
            out.flush();
            try (ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

                while (true) {
                    Request request = (Request) in.readObject();
                    Command cmd = request.getCommand();

                    if (cmd == Command.BYE) {
                        out.writeObject(Response.ok("BYE"));
                        out.flush();
                        System.out.println("Client sent BYE; closing connection.");
                        break;
                    }

                    Response response = handleRequest(cmd, request.getData());
                    out.writeObject(response);
                    out.flush();
                    out.reset();
                }
            }
        } catch (java.io.EOFException eof) {
            System.out.println("Client disconnected.");
        } catch (Exception e) {
            System.err.println("ClientHandler error: " + e.getMessage());
        }
    }

    private Response handleRequest(Command cmd, Object data) {
        boolean isWrite = cmd.requiresLock();
        if (isWrite) {
            DatabaseLock.WRITE_LOCK.lock();
        }
        try {
            switch (cmd) {
                case PING:
                    return Response.ok("PONG");

                case LOGIN: {
                    LoginDTO login = (LoginDTO) data;
                    User u = userDAO.loginUser(login.getUsername(), login.getPassword());
                    return (u != null)
                            ? Response.ok(userDAO.toDTO(u))
                            : Response.error(Status.INVALID_CREDENTIALS, "Wrong username or password");
                }
                case REGISTER: {
                    UserDTO dto = (UserDTO) data;
                    if (userDAO.isUsernameTaken(dto.getUsername())) {
                        return Response.error(Status.INVALID_ARGUMENT, "Username already taken");
                    }
                    User newUser = userDAO.fromDTO(dto);
                    String role = (newUser instanceof Subscriber) ? "SUBSCRIBER" : "ARTIST";
                    if (!userDAO.registerUser(newUser, newUser.getPassword(), role)) {
                        return Response.error(Status.INTERNAL_ERROR, "Could not register user");
                    }
                    if (newUser instanceof Subscriber sub) {
                        subscriberDAO.insertSubscriberDetails(sub);
                    } else if (newUser instanceof Artist art) {
                        artistDAO.insertArtistDetails(art);
                    }
                    return Response.ok("Success");
                }

                case GET_ADDRESS: {
                    Address a = addressDAO.getAddressById((int) data);
                    return (a != null) ? Response.ok(addressDAO.toDTO(a))
                            : Response.error(Status.NOT_FOUND, "Address not found");
                }
                case ADD_ADDRESS:
                    addressDAO.insertAddress(addressDAO.fromDTO((AddressDTO) data));
                    return Response.ok();
                case REMOVE_ADDRESS:
                    addressDAO.deleteAddress((int) data);
                    return Response.ok();
                case UPDATE_ADDRESS:
                    addressDAO.updateAddress(addressDAO.fromDTO((AddressDTO) data));
                    return Response.ok();
                case GET_ALL_ADDRESSES:
                    return Response.ok(addressDAO.getAllAddresses().stream()
                            .map(addressDAO::toDTO).toList());

                case GET_ARTIST: {
                    Artist a = artistDAO.getArtistById((int) data);
                    return (a != null) ? Response.ok(artistDAO.toDTO(a))
                            : Response.error(Status.NOT_FOUND, "Artist not found");
                }
                case ADD_ARTIST:
                    artistDAO.insertArtistDetails(artistDAO.fromDTO((ArtistDTO) data));
                    return Response.ok();
                case REMOVE_ARTIST:
                    artistDAO.deleteArtistDetails((int) data);
                    return Response.ok();
                case UPDATE_ARTIST:
                    artistDAO.updateArtistDetails(artistDAO.fromDTO((ArtistDTO) data));
                    return Response.ok();

                case ADD_ART_PRODUCT:
                    return Response.error(Status.INVALID_ARGUMENT,
                            "Use ADD_PHYSICAL_PRODUCT or ADD_DIGITAL_PRODUCT");
                case GET_ART_PRODUCT: {
                    ArtProduct p = artProductDAO.getArtProductById((int) data);
                    return (p != null) ? Response.ok(artProductDAO.toDTO(p))
                            : Response.error(Status.NOT_FOUND, "Art product not found");
                }
                case REMOVE_ART_PRODUCT:
                    artProductDAO.deleteArtProduct((int) data);
                    return Response.ok();
                case UPDATE_ART_PRODUCT:
                    return Response.error(Status.INVALID_ARGUMENT,
                            "Use UPDATE_PHYSICAL_PRODUCT or UPDATE_DIGITAL_PRODUCT");

                case ADD_DIGITAL_PRODUCT:
                    return Response.ok(digitalProductDAO.insertDigitalProduct(
                            digitalProductDAO.fromDTO((DigitalProductDTO) data)));
                case GET_DIGITAL_PRODUCT: {
                    DigitalProduct p = digitalProductDAO.getDigitalProductById((int) data);
                    return (p != null) ? Response.ok(digitalProductDAO.toDTO(p))
                            : Response.error(Status.NOT_FOUND, "Digital product not found");
                }
                case REMOVE_DIGITAL_PRODUCT:
                    digitalProductDAO.deleteDigitalProduct((int) data);
                    return Response.ok();
                case UPDATE_DIGITAL_PRODUCT:
                    digitalProductDAO.updateDigitalProduct(digitalProductDAO.fromDTO((DigitalProductDTO) data));
                    return Response.ok();

                case ADD_DIMENSION: {
                    int newId = dimensionsDAO.insertDimensions(dimensionsDAO.fromDTO((DimensionsDTO) data));
                    return (newId != -1) ? Response.ok(newId)
                            : Response.error(Status.INTERNAL_ERROR, "Could not insert dimensions");
                }
                case GET_DIMENSION: {
                    Dimensions d = dimensionsDAO.getDimensionsById((int) data);
                    return (d != null) ? Response.ok(dimensionsDAO.toDTO(d))
                            : Response.error(Status.NOT_FOUND, "Dimensions not found");
                }
                case REMOVE_DIMENSION:
                    dimensionsDAO.deleteDimensions((int) data);
                    return Response.ok();
                case UPDATE_DIMENSION: {
                    DimensionsDTO dto = (DimensionsDTO) data;
                    dimensionsDAO.updateDimensions(dto.getId(), dimensionsDAO.fromDTO(dto));
                    return Response.ok();
                }

                case ADD_PHYSICAL_PRODUCT:
                    return Response.ok(physicalProductDAO.insertPhysicalProduct(
                            physicalProductDAO.fromDTO((PhysicalProductDTO) data)));
                case GET_PHYSICAL_PRODUCT: {
                    PhysicalProduct p = physicalProductDAO.getPhysicalProductById((int) data);
                    return (p != null) ? Response.ok(physicalProductDAO.toDTO(p))
                            : Response.error(Status.NOT_FOUND, "Physical product not found");
                }
                case REMOVE_PHYSICAL_PRODUCT:
                    physicalProductDAO.deletePhysicalProduct((int) data);
                    return Response.ok();
                case UPDATE_PHYSICAL_PRODUCT:
                    physicalProductDAO.updatePhysicalProduct(physicalProductDAO.fromDTO((PhysicalProductDTO) data));
                    return Response.ok();

                case ADD_SUBSCRIBER:
                    subscriberDAO.insertSubscriberDetails(subscriberDAO.fromDTO((SubscriberDTO) data));
                    return Response.ok();
                case GET_SUBSCRIBER: {
                    Subscriber s = subscriberDAO.getSubscriberById((int) data);
                    return (s != null) ? Response.ok(subscriberDAO.toDTO(s))
                            : Response.error(Status.NOT_FOUND, "Subscriber not found");
                }
                case REMOVE_SUBSCRIBER:
                    subscriberDAO.deleteSubscriberDetails((int) data);
                    return Response.ok();
                case UPDATE_SUBSCRIBER:
                    subscriberDAO.updateSubscriberDetails(subscriberDAO.fromDTO((SubscriberDTO) data));
                    return Response.ok();

                case GET_USER: {
                    User u = userDAO.getUserById((int) data);
                    return (u != null) ? Response.ok(userDAO.toDTO(u))
                            : Response.error(Status.NOT_FOUND, "User not found");
                }
                case REMOVE_USER:
                    userDAO.deleteUser((int) data);
                    return Response.ok();
                case UPDATE_USER: {
                    User u = userDAO.fromDTO((UserDTO) data);
                    userDAO.updateUser(u);
                    if (u instanceof Subscriber sub) {
                        subscriberDAO.updateSubscriberDetails(sub);
                    }
                    return Response.ok();
                }

                case ADD_SUBSCRIPTION: {
                    Subscription s = subscriptionDAO.fromDTO((SubscriptionDTO) data);
                    if (subscriptionDAO.exists(s.getSubscriber().getId(), s.getArtist().getId())) {
                        return Response.error(Status.INVALID_ARGUMENT, "Subscription already exists");
                    }
                    subscriptionDAO.insertSubscription(s.getSubscriber(), s.getArtist());
                    return Response.ok();
                }
                case GET_SUBSCRIPTION: {
                    Subscription s = subscriptionDAO.getSubscriptionById((int) data);
                    return (s != null) ? Response.ok(subscriptionDAO.toDTO(s))
                            : Response.error(Status.NOT_FOUND, "Subscription not found");
                }
                case REMOVE_SUBSCRIPTION: {
                    SubscriptionDTO dto = (SubscriptionDTO) data;
                    subscriptionDAO.deleteSubscription(dto.getSubscriberId(), dto.getArtistId());
                    return Response.ok();
                }
                case UPDATE_SUBSCRIPTION:
                    subscriptionDAO.updateSubscription(subscriptionDAO.fromDTO((SubscriptionDTO) data));
                    return Response.ok();

                case ADD_PURCHASE: {
                    PurchaseDTO p = (PurchaseDTO) data;
                    int productId = p.getProductId();


                    if ("PHYSICAL".equalsIgnoreCase(p.getProductType())) {
                        PhysicalProduct phys = physicalProductDAO.getPhysicalProductById(productId);
                        if (phys == null) {
                            return Response.error(Status.NOT_FOUND, "Physical product not found");
                        }
                        if (phys.getStock() <= 0) {
                            return Response.error(Status.INVALID_ARGUMENT, "Product sold out");
                        }
                        purchaseDAO.insertPurchase(p.getBuyerId(), productId, "PHYSICAL");
                        physicalProductDAO.updateStock(productId, phys.getStock() - 1);
                        return Response.ok("Purchased physical product; remaining stock = "
                                + (phys.getStock() - 1));
                    } else if ("DIGITAL".equalsIgnoreCase(p.getProductType())) {
                        DigitalProduct digi = digitalProductDAO.getDigitalProductById(productId);
                        if (digi == null) {
                            return Response.error(Status.NOT_FOUND, "Digital product not found");
                        }
                        purchaseDAO.insertPurchase(p.getBuyerId(), productId, "DIGITAL");
                        return Response.ok("Purchased digital product");
                    }

                    return Response.error(Status.INVALID_ARGUMENT,
                            "productType must be PHYSICAL or DIGITAL");
                }

                case GET_ALL_USERS:
                    return Response.ok(userDAO.getAllUsers().stream().map(userDAO::toDTO).toList());
                case GET_ALL_ARTISTS:
                    return Response.ok(artistDAO.getAllArtists().stream().map(artistDAO::toDTO).toList());
                case GET_ALL_SUBSCRIBERS:
                    return Response.ok(subscriberDAO.getAllSubscribers().stream().map(subscriberDAO::toDTO).toList());
                case GET_ALL_ART_PRODUCTS:
                    return Response.ok(artProductDAO.getAllArtProducts().stream().map(artProductDAO::toDTO).toList());
                case GET_ALL_DIGITAL_PRODUCTS:
                    return Response.ok(digitalProductDAO.getAllDigitalProducts().stream().map(digitalProductDAO::toDTO).toList());
                case GET_ALL_PHYSICAL_PRODUCTS:
                    return Response.ok(physicalProductDAO.getAllPhysicalProducts().stream().map(physicalProductDAO::toDTO).toList());
                case GET_ALL_DIMENSIONS:
                    return Response.ok(dimensionsDAO.getAllDimensions().stream().map(dimensionsDAO::toDTO).toList());
                case GET_ALL_SUBSCRIPTIONS:
                    return Response.ok(subscriptionDAO.getAllSubscriptions().stream().map(subscriptionDAO::toDTO).toList());

                case GET_SALES_LOGS_BY_ARTIST: {
                    int artistId = (int) data;
                    java.util.List<SalesLogDTO> logs = new java.util.ArrayList<>();
                    for (PurchaseDAO.PurchaseRow row : purchaseDAO.getAllPurchases()) {

                        ArtProduct product = "PHYSICAL".equalsIgnoreCase(row.productType())
                                ? physicalProductDAO.getPhysicalProductById(row.productId())
                                : digitalProductDAO.getDigitalProductById(row.productId());
                        if (product == null || product.getArtist() == null
                                || product.getArtist().getId() != artistId) {
                            continue;
                        }
                        User buyer = userDAO.getUserById(row.buyerId());
                        String buyerName = (buyer != null) ? buyer.getUsername() : "unknown";
                        logs.add(new SalesLogDTO(buyerName, product.getName(),
                                row.productType(), row.date()));
                    }
                    return Response.ok(logs);
                }

                default:
                    return Response.error(Status.INVALID_ARGUMENT, "Unknown command: " + cmd);
            }
        } catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            return Response.error(Status.INVALID_ARGUMENT, "Bad payload: " + e.getMessage());
        } catch (Exception e) {
            return Response.error(Status.INTERNAL_ERROR, e.getMessage());
        } finally {
            if (isWrite) {
                DatabaseLock.WRITE_LOCK.unlock();
            }
        }
    }
}
