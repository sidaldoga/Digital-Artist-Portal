package model;

import java.util.Objects;

public class PhysicalProduct extends ArtProduct {
    private ArtStyle style;
    private Dimensions dimensionsCm;
    private double weight;
    private int stock;
    private MediumType medium;
    private ProductType productType;

    public PhysicalProduct(int id, String name, String description, double basePrice, Artist artist, ArtStyle style, ProductType productType, MediumType medium, int stock, double weight, Dimensions dimensionsCm) {
        super(id, name, description, basePrice, artist);
        this.style = style;
        this.productType = productType;
        this.medium = medium;
        this.stock = stock;
        this.weight = weight;
        this.dimensionsCm = dimensionsCm;
    }


    public ArtStyle getStyle() {
        return style;
    }

    public void setStyle(ArtStyle style) {
        this.style = style;
    }

    public Dimensions getDimensionsCm() {
        return dimensionsCm;
    }

    public void setDimensionsCm(Dimensions dimensionsCm) {
        this.dimensionsCm = dimensionsCm;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public MediumType getMedium() {
        return medium;
    }

    public void setMedium(MediumType medium) {
        this.medium = medium;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public String toString(){
        String artistInfo = (getArtist() != null) ? getArtist().getUsername() : "null";

        return " {"+"ID="+getId()+", Name="+getName()+"\n"
                + "Description="+getDescription()+"\n"
                + "Price="+getBasePrice()+"\n"
                + "Artist= "+ artistInfo +"\n"
                + "Style="+style+"\n"
                + "Dimensions(Cm)="+dimensionsCm+" Weight="+weight+" Stock="+stock+" Product Type="+productType+" Medium Type="+medium+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this == object) return true;
        if (!(object instanceof PhysicalProduct physicalproduct)) return false;

        if (!super.equals(object)) return false;

        return stock == physicalproduct.stock
                && Double.compare(physicalproduct.weight, weight) == 0
                && style == physicalproduct.style
                && Objects.equals(dimensionsCm, physicalproduct.dimensionsCm)
                && productType == physicalproduct.productType
                && medium == physicalproduct.medium;
    }

    @Override
    public int hashCode(){
        return Objects.hash(super.hashCode(), style, dimensionsCm, weight, stock, productType, medium);
    }
}
