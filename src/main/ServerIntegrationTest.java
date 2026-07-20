package main;

import network.Command;
import network.Response;
import network.ServerConnection;
import network.Status;
import network.dto.*;
import network.Client;


public class ServerIntegrationTest {


    public void testClientDynamicFlow() {
        try (Client client = new Client("localhost", 8080)) {

            System.out.println("PING-> " + client.ping());

            Response login = client.login("artbysidal", "KatzeHund2001");
            System.out.println("LOGIN-> " + login);

            Response addresses = client.getAllAddresses();
            System.out.println("GET_ALL_ADDRESSES -> " + addresses.getStatus()
                    + " (" + payloadSize(addresses) + " addresses)");

            System.out.println("GET_ADDRESS 1-> " + client.getAddress(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        String tag = String.valueOf(System.currentTimeMillis() % 1_000_000);
        String artistUser = "artist_" + tag;
        String subUser = "sub_" + tag;
        String password = "secret123";

        try (ServerConnection conn = new ServerConnection("localhost", 8080)) {
            System.out.println("Connected to server. Running integration test\n");
            check("PING", conn.send(Command.PING, null));

            ArtistDTO artistReg = new ArtistDTO(0, "Sidal", "Ozkan", artistUser,
                    artistUser + "@art.com", "ARTIST", true,"12345");
            artistReg.setPassword(password);
            check("REGISTER artist", conn.send(Command.REGISTER, artistReg));

            Response login = conn.send(Command.LOGIN, new LoginDTO(artistUser, password));
            check("LOGIN artist", login);
            ArtistDTO artist = (ArtistDTO) login.getPayload();
            int artistId = artist.getId();
            System.out.println("      -> logged-in artist id = " + artistId + "\n");


            check("ADD_ADDRESS", conn.send(Command.ADD_ADDRESS,
                    new AddressDTO("Meistersingerstr. 42", "Essen", "45307", "Germany")));

            Response dim = conn.send(Command.ADD_DIMENSION, new DimensionsDTO(20.0, 30.0, 2.0));
            check("ADD_DIMENSION", dim);
            System.out.println("      -> new dimension id = " + dim.getPayload() + "\n");

            PhysicalProductDTO phys = new PhysicalProductDTO(0, artistId, "Forest Canvas",
                    "Oil painting of a rain forest", 150.0, artistUser,
                    "REALISM", 1, 2.5, "OIL_PAINTING", "CANVAS",
                    new DimensionsDTO(20.0, 30.0, 2.0));
            Response physResp = conn.send(Command.ADD_PHYSICAL_PRODUCT, phys);
            check("ADD_PHYSICAL_PRODUCT (stock=1)", physResp);
            int physId = (int) physResp.getPayload();

            DigitalProductDTO digi = new DigitalProductDTO(0, artistId, "Decorative Poster",
                    "Poster for your kitchen", 12.0, artistUser,
                    "SURREALISM", "https://download.portal.com/f234", 27.4, ".png", "POSTER");
            Response digiResp = conn.send(Command.ADD_DIGITAL_PRODUCT, digi);
            check("ADD_DIGITAL_PRODUCT", digiResp);
            int digiId = (int) digiResp.getPayload();

            SubscriberDTO subReg = new SubscriberDTO(0, "Nermin", "Bozkurt", subUser,
                    subUser + "@mail.com", "SUBSCRIBER", 0,
                    new AddressDTO("Hermannstr. 5", "Duisburg", "47226", "Germany"),"12345");
            subReg.setPassword(password);
            check("REGISTER subscriber", conn.send(Command.REGISTER, subReg));

            Response subLogin = conn.send(Command.LOGIN, new LoginDTO(subUser, password));
            check("LOGIN subscriber", subLogin);
            int subId = ((SubscriberDTO) subLogin.getPayload()).getId();

            SubscriptionDTO subscription = new SubscriptionDTO(0, subId, subUser,
                    artistId, artistUser, true);
            check("ADD_SUBSCRIPTION", conn.send(Command.ADD_SUBSCRIPTION, subscription));

            expect("ADD_SUBSCRIPTION duplicate",
                    conn.send(Command.ADD_SUBSCRIPTION, subscription), Status.INVALID_ARGUMENT);

            check("ADD_PURCHASE physical",
                    conn.send(Command.ADD_PURCHASE, new PurchaseDTO(subId, physId, "PHYSICAL")));
            expect("ADD_PURCHASE physical",
                    conn.send(Command.ADD_PURCHASE, new PurchaseDTO(subId, physId, "PHYSICAL")),
                    Status.INVALID_ARGUMENT);
            check("ADD_PURCHASE digital",
                    conn.send(Command.ADD_PURCHASE, new PurchaseDTO(subId, digiId, "DIGITAL")));
            check("ADD_PURCHASE digital",
                    conn.send(Command.ADD_PURCHASE, new PurchaseDTO(subId, digiId, "DIGITAL")));

            Response getArtist = conn.send(Command.GET_USER, artistId);
            check("GET_USER (artist)", getArtist);

            Response getSub = conn.send(Command.GET_SUBSCRIBER, subId);
            check("GET_SUBSCRIBER", getSub);

            Response addresses = conn.send(Command.GET_ALL_ADDRESSES, null);
            check("GET_ALL_ADDRESSES", addresses);
            System.out.println("      -> addresses on record: "
                    + ((java.util.List<?>) addresses.getPayload()).size());

        } catch (Exception e) {
            failed++;
            System.err.println("Test aborted by exception: " + e);
            e.printStackTrace();
        }

        System.out.println("\nRESULT");
        System.out.println("PASSED: " + passed + "   FAILED: " + failed);
        System.out.println(failed == 0 ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
    }

    private static void check(String label, Response response) {
        boolean ok = response != null && response.getStatus() == Status.OK;
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.println("[" + (ok ? "PASS" : "FAIL") + "] " + label + " -> " + response);
    }
    private static void expect(String label, Response response, Status expected) {
        boolean ok = response != null && response.getStatus() == expected;
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.println("[" + (ok ? "PASS" : "FAIL") + "] " + label
                + " (expected " + expected + ") -> " + response);
    }

    private int payloadSize(Response r) {
        return (r.getPayload() instanceof java.util.List<?> list) ? list.size() : 0;
    }
}
