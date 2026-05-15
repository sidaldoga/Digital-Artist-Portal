package model;

import java.util.Objects;

public class ArtProduct {
    private String id;
    private String name;
    private String description;
    private float basePrice;
    private String artistId;

    public ArtProduct(String id, String name, String description, float basePrice, String artistId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.artistId = artistId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public String toString(){
        return "ArtProduct {"+"Id="+id+", Name="+name+"\n" + "Description="+description+"\n" + "Base Price="+basePrice+"\n" + "Artist="+artistId+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof ArtProduct artproduct))return false;
        return Objects.equals(id,artproduct.id) && Objects.equals(name, artproduct.name) && Objects.equals(description,artproduct.description)&&Objects.equals(basePrice,artproduct.basePrice) &&Objects.equals(artistId,artproduct.artistId);

    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,description,basePrice,artistId);
    }



}
