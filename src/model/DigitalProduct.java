package model;

import java.util.Objects;

public class DigitalProduct extends ArtProduct {
    private ArtStyle style;
    private String downloadURL;
    private double fileSizeMb;
    private String fileExtension;
    private DigProductType digProductType;

    public DigitalProduct(String id, String name, String description, float basePrice, String artistId, ArtStyle style, DigProductType digProductType, String fileExtension, double fileSizeMb, String downloadURL) {
        super(id, name, description, basePrice, artistId);
        this.style = style;
        this.digProductType = digProductType;
        this.fileExtension = fileExtension;
        this.fileSizeMb = fileSizeMb;
        this.downloadURL = downloadURL;
    }

    public ArtStyle getStyle() {
        return style;
    }

    public void setStyle(ArtStyle style) {
        this.style = style;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public DigProductType getDigProductType() {
        return digProductType;
    }

    public void setDigProductType(DigProductType digProductType) {
        this.digProductType = digProductType;
    }

    @Override
    public String toString(){
        return " {"+"ID="+getId()+", Name="+getName()+"\n" + "Description="+getDescription()+"\n" + "Price="+getBasePrice()+"\n" + "Artist= "+getArtistId()+"\n" + "Style="+style+"\n" + "DownloadUrl="+downloadURL+"\n" + "FileSizeMb="+fileSizeMb+ "FileExtension="+fileExtension+"Digital Product Type="+digProductType+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof DigitalProduct digitalproduct))return false;
        return Objects.equals(getId(),digitalproduct.getId()) && Objects.equals(getName(), digitalproduct.getName()) && Objects.equals(getDescription(),digitalproduct.getDescription())&& Objects.equals(getBasePrice(), digitalproduct.getBasePrice()) && Objects.equals(getArtistId(), digitalproduct.getArtistId()) && Objects.equals(style,digitalproduct.style)&&Objects.equals(downloadURL,digitalproduct.downloadURL)&& Objects.equals(fileSizeMb,digitalproduct.fileSizeMb) && Objects.equals(fileExtension,digitalproduct.fileExtension)&& Objects.equals(digProductType,digitalproduct.digProductType);

    }

    @Override
    public int hashCode(){
        return Objects.hash(getId(),getName(),getDescription(),getBasePrice(),getArtistId(),style,downloadURL,fileSizeMb,fileExtension,digProductType);
    }
}
