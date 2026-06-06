package model;

import java.util.Objects;

public abstract class ArtProduct {
    private int id;
    private String name;
    private String description;
    private double basePrice;
    private Artist artist;

    public ArtProduct(int id, String name, String description, double basePrice, Artist artist) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Artist getArtist() { return artist; }
    
    public void setArtist(Artist artist) { this.artist = artist; }

    @Override
    public String toString() {
        String artistName = (artist != null) ? artist.getUsername() : "null";

        return "ArtProduct {" +
                "Id=" + id +
                ", Name=" + name +
                "\nDescription=" + description +
                "\nBase Price=" + basePrice +
                "\nArtist=" + artistName +
                "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ArtProduct artproduct)) return false;
        return id == artproduct.id &&
                Double.compare(artproduct.basePrice, basePrice) == 0 &&
                Objects.equals(name, artproduct.name) &&
                Objects.equals(description, artproduct.description) &&
                Objects.equals(artist, artproduct.artist);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,description,basePrice,artist);
    }



}
