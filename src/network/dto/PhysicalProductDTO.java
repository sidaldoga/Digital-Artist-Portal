package network.dto;


import java.io.Serializable;

public class PhysicalProductDTO implements Serializable {
    private int id;
    private int artistId;
    private String name;
    private String description;
    private double basePrice;
    private String artistUsername;
    private String style;
    private int stock;
    private double weight;
    private String medium;
    private String productType;

    private DimensionsDTO dimensions;

    public PhysicalProductDTO(int id, int artistId, String name, String description, double basePrice,
                              String artistUsername, String style, int stock, double weight,
                              String medium, String productType, DimensionsDTO dimensions) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.artistUsername = artistUsername;
        this.style = style;
        this.stock = stock;
        this.weight = weight;
        this.medium = medium;
        this.productType = productType;
        this.dimensions = dimensions;
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

    public String getStyle() {
        return style;
    }

    public int getStock() {
        return stock;
    }

    public double getWeight() {
        return weight;
    }

    public String getMedium() {
        return medium;
    }

    public String getProductType() {
        return productType;
    }

    public DimensionsDTO getDimensions() {
        return dimensions;
    }
}