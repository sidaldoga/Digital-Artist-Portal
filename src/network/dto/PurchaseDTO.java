package network.dto;

import java.io.Serializable;

public class PurchaseDTO implements Serializable {
    private int buyerId;
    private int productId;
    private String productType;

    public PurchaseDTO(int buyerId, int productId, String productType) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.productType = productType;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductType() {
        return productType;
    }

    @Override
    public String toString() {
        return "Purchase{buyerId=" + buyerId + ", productId=" + productId
                + ", productType='" + productType + "'}";
    }
}
