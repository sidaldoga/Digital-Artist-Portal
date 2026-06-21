package main;

import db.*;
import db.dao.*;
import model.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("DIGITAL ART PORTAL INITIALIZATION");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        AddressDAO addressDAO = new AddressDAO(dbConnection);
        DimensionsDAO dimensionsDAO = new DimensionsDAO(dbConnection);

        UserDAO userDAO = new UserDAO(dbConnection);


        SubscriberDAO subscriberDAO = new SubscriberDAO(dbConnection, userDAO);
        ArtistDAO artistDAO = new ArtistDAO(dbConnection, userDAO);

        ArtProductDAO artProductDAO = new ArtProductDAO(dbConnection, userDAO);

        PhysicalProductDAO physicalProductDAO = new PhysicalProductDAO(dbConnection, artProductDAO);
        DigitalProductDAO digitalProductDAO = new DigitalProductDAO(dbConnection, artProductDAO);

        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(dbConnection, userDAO);

        try {
            System.out.println("\n Inserting 5 Addresses");
            Address addr1 = new Address("Meistersingerstr. 42", "Essen", "45307", "Germany");
            Address addr2 = new Address("Hermannstr. 5", "Duisburg", "47226", "Germany");
            Address addr3 = new Address("Königsallee 60", "Düsseldorf", "40212", "Germany");
            Address addr4 = new Address("Münzstr. 2", "Münster", "48143", "Germany");
            Address addr5 = new Address("Saarlandstr. 31", "Dortmund", "44139", "Germany");

            addressDAO.insertAddress(addr1);
            addressDAO.insertAddress(addr2);
            addressDAO.insertAddress(addr3);
            addressDAO.insertAddress(addr4);
            addressDAO.insertAddress(addr5);

            System.out.println("\n Inserting 5 Dimensions");
            Dimensions dim1 = new Dimensions(20.0, 30.0, 2.0);
            Dimensions dim2 = new Dimensions(50.0, 70.0, 5.0);
            Dimensions dim3 = new Dimensions(10.0, 15.0, 0.5);
            Dimensions dim4 = new Dimensions(40.0, 40.0, 4.0);
            Dimensions dim5 = new Dimensions(100.0, 120.0, 10.0);

            dimensionsDAO.insertDimensions(dim1);
            dimensionsDAO.insertDimensions(dim2);
            dimensionsDAO.insertDimensions(dim3);
            dimensionsDAO.insertDimensions(dim4);
            dimensionsDAO.insertDimensions(dim5);

            System.out.println("\n Registering and Inserting 5 Users");
            Artist artist1 = new Artist("Sidal", "Özkan", "artbysidal", "KatzeHund2001", "sidal@art.com", true);
            Artist artist2 = new Artist("Dilek", "Kayaturan", "dilek_art", "secure3040", "dilek@art.com", false);

            userDAO.registerUser(artist1, artist1.getPassword(), "ARTIST");
            userDAO.registerUser(artist2, artist2.getPassword(), "ARTIST");

            User loggedArt1 = userDAO.loginUser("artbysidal", "KatzeHund2001");
            if (loggedArt1 != null) { artist1.setId(loggedArt1.getId()); artistDAO.insertArtistDetails(artist1); }
            User loggedArt2 = userDAO.loginUser("dilek_art", "secure3040");
            if (loggedArt2 != null) { artist2.setId(loggedArt2.getId()); artistDAO.insertArtistDetails(artist2); }

            Subscriber sub1 = new Subscriber("Nermin", "Bozkurt", "nrmnkedicik", "no.pass1234", addr1, new ArrayList<>(), 0, "nermin@mail.com");
            Subscriber sub2 = new Subscriber("Yaren", "Altunal", "altunalya", "thailand.2026", addr2, new ArrayList<>(), 50, "yaren@mail.com");
            Subscriber sub3 = new Subscriber("Serkut", "Tirasoglu", "sekotir", "ecology!02", addr3, new ArrayList<>(), 100, "serkut@mail.com");

            userDAO.registerUser(sub1, sub1.getPassword(), "SUBSCRIBER");
            userDAO.registerUser(sub2, sub2.getPassword(), "SUBSCRIBER");
            userDAO.registerUser(sub3, sub3.getPassword(), "SUBSCRIBER");

            User loggedSub1 = userDAO.loginUser("nrmnkedicik", "no.pass1234");
            if (loggedSub1 != null) { sub1.setId(loggedSub1.getId()); subscriberDAO.insertSubscriberDetails(sub1); }
            User loggedSub2 = userDAO.loginUser("altunalya", "thailand.2026");
            if (loggedSub2 != null) { sub2.setId(loggedSub2.getId()); subscriberDAO.insertSubscriberDetails(sub2); }
            User loggedSub3 = userDAO.loginUser("sekotir", "ecology!02");
            if (loggedSub3 != null) { sub3.setId(loggedSub3.getId()); subscriberDAO.insertSubscriberDetails(sub3); }

            System.out.println("\n Inserting 5 ArtProducts");
            PhysicalProduct phys1 = new PhysicalProduct(0, "Forest Canvas", "Oil painting of a rain forest", 150.0, artist1, ArtStyle.REALISM, ProductType.CANVAS, MediumType.OIL_PAINTING, 5, 2.5, dim1);
            PhysicalProduct phys2 = new PhysicalProduct(0, "Whimsical Sticker", "Glitter stickers", 15.0, artist1, ArtStyle.SURREALISM, ProductType.STICKER, MediumType.WATERCOLOR, 100, 0.1, dim3);
            PhysicalProduct phys3 = new PhysicalProduct(0, "Milan Postcard", "Hand-drawn postcard", 5.0, artist2, ArtStyle.REALISM, ProductType.POSTCARD, MediumType.PASTEL, 50, 0.2, dim3);

            physicalProductDAO.insertPhysicalProduct(phys1);
            physicalProductDAO.insertPhysicalProduct(phys2);
            physicalProductDAO.insertPhysicalProduct(phys3);

            DigitalProduct dig1 = new DigitalProduct(0, "Decorative Poster", "Poster for your kitchen", 12.0f, artist1, ArtStyle.SURREALISM, DigProductType.POSTER, ".png", 27.4, "https://download.portal.com/f234");
            DigitalProduct dig2 = new DigitalProduct(0, "Custom Portrait", "Portraits of you and your loved ones", 75.0f, artist2, ArtStyle.REALISM, DigProductType.POSTER, ".png", 45.2, "https://download.portal.com/b567");

            digitalProductDAO.insertDigitalProduct(dig1);
            digitalProductDAO.insertDigitalProduct(dig2);

            System.out.println("\n Inserting 5 Subscriptions & Testing Artist Quota");
            subscriptionDAO.insertSubscription(sub1, artist1);
            subscriptionDAO.insertSubscription(sub2, artist1);
            subscriptionDAO.insertSubscription(sub3, artist1);
            subscriptionDAO.insertSubscription(sub1, artist2);
            subscriptionDAO.insertSubscription(sub2, artist2);

            System.out.println("\n Testing Relationships & Operations");
            int activeSubs = subscriptionDAO.getSubscriptionCountForArtist(artist1.getId());
            System.out.println("Active Followers/Subscriptions for Artist 'artbysidal' (ID: " + artist1.getId() + "): " + activeSubs);

            System.out.println("Trying to login with correct credentials.");
            User authUser = userDAO.loginUser("nrmnkedicik", "no.pass1234");
            if (authUser != null) {
                System.out.println(" Auth Success! Welcome, " + authUser.getFname() + " " + authUser.getLname());
            }

            System.out.println("Trying to login with WRONG password.");
            userDAO.loginUser("nrmnkedicik", "wrong_password_123");

            System.out.println("Registering a new user with unique username check.");
            String candidateUsername = "new_artist_guest";
            if (userDAO.isUsernameTaken(candidateUsername)) {
                System.out.println("Registration Aborted: Username '" + candidateUsername + "' already exists.");
            } else {
                Artist guest = new Artist("Demir", "Özkan", candidateUsername, "password123", "demos@mail.com", true);
                userDAO.registerUser(guest, "password123", "ARTIST");
            }

        } catch (Exception e) {
            System.err.println("An error occurred during testing: " + e.getMessage());
        } finally {
            System.out.println("\nClosing DB Connection");
            dbConnection.closeConnection();
        }
        System.out.println("Digital Art Portal Testing Completed Successfully");
    }
}