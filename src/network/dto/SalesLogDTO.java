package network.dto;

import java.io.Serializable;

public class SalesLogDTO implements Serializable {
    private final String buyerName;
    private final String productName;
    private final String productType;
    private final String purchaseDate;

    public SalesLogDTO(String buyerName, String productName, String productType, String purchaseDate) {
        this.buyerName = buyerName;
        this.productName = productName;
        this.productType = productType;
        this.purchaseDate = purchaseDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    @Override
    public String toString() {
        return "SalesLog{buyer='" + buyerName + "', product='" + productName
                + "', type='" + productType + "', date='" + purchaseDate + "'}";
    }
}
