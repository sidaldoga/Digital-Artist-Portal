package network.dto;

import java.io.Serializable;

public class DigitalProductDTO implements Serializable {
    private int id;
    private int artistId;
    private String name;
    private String description;
    private double basePrice;
    private String artistUsername;
    private String style;
    private String downloadUrl;
    private double fileSizeMb;
    private String fileExtension;
    private String digProductType;

    public DigitalProductDTO(int id, int artistId, String name, String description, double basePrice, String artistUsername,
                             String style, String downloadUrl, double fileSizeMb, String fileExtension, String digProductType) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.artistUsername = artistUsername;
        this.style = style;
        this.downloadUrl = downloadUrl;
        this.fileSizeMb = fileSizeMb;
        this.fileExtension = fileExtension;
        this.digProductType = digProductType;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getDigProductType() {
        return digProductType;
    }

    @Override
    public String toString() {
        return "DigitalProduct{id=" + id + ", name='" + name + "', basePrice=" + basePrice
                + ", artistUsername='" + artistUsername + "', style='" + style
                + "', digProductType='" + digProductType + "', fileExtension='" + fileExtension
                + "', fileSizeMb=" + fileSizeMb + ", downloadUrl='" + downloadUrl + "'}";
    }
}