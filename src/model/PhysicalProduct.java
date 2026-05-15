package model;

import java.util.Objects;

public class PhysicalProduct extends ArtProduct {
    private String style;
    private String dimensionsCm;
    private float weight;
    private int stock;
    private ProductType productType;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDimensionsCm() {
        return dimensionsCm;
    }

    public void setDimensionsCm(String dimensionsCm) {
        this.dimensionsCm = dimensionsCm;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public PhysicalProduct(String id, String name, String description, float basePrice, String artistId, String style, String dimensionsCm, float weight, int stock, ProductType productType) {
        super(id,name,description,basePrice,artistId);
        this.style = style;
        this.dimensionsCm = dimensionsCm;
        this.weight = weight;
        this.stock = stock;
        this.productType = productType;


    }

    @Override
    public String toString(){
        return " {"+"ID="+getId()+", Name="+getName()+"\n" + "Description="+getDescription()+"\n" + "Price="+getBasePrice()+"\n" + "Artist= "+getArtistId()+"\n" + "Style="+style+"\n" + "Dimensions(Cm)="+dimensionsCm+"\"Weight="+weight+"Stock="+stock+"Product Type="+productType+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof PhysicalProduct physicalproduct))return false;
        return Objects.equals(getId(),physicalproduct.getId()) && Objects.equals(getName(), physicalproduct.getName()) && Objects.equals(getDescription(),physicalproduct.getDescription())&& Objects.equals(getBasePrice(), physicalproduct.getBasePrice()) && Objects.equals(getArtistId(), physicalproduct.getArtistId()) && Objects.equals(style,physicalproduct.style)&&Objects.equals(dimensionsCm,physicalproduct.dimensionsCm)&& Objects.equals(weight,physicalproduct.weight) && Objects.equals(stock,physicalproduct.stock)&& Objects.equals(productType,physicalproduct.productType);

    }

    @Override
    public int hashCode(){
        return Objects.hash(getId(),getName(),getDescription(),getBasePrice(),getArtistId(),style,dimensionsCm,weight,stock,productType);
    }
}
