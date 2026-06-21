package network.dto;

import java.io.Serializable;

public class ArtProductDTO implements Serializable {
    private int id;
    private int artistId;
    private String name;
    private String description;
    private double basePrice;
    private String artistUsername;

    public ArtProductDTO(int id, int artistId, String name, String description, double basePrice, String artistUsername) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.artistUsername = artistUsername;
    }

    public int getId() {
        return id;
    }

    public int getArtistId() {
        return artistId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getArtistUsername() {
        return artistUsername;
    }
}