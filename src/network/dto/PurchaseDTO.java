package network.dto;

import java.io.Serializable;

public class PurchaseDTO implements Serializable {
    private int buyerId;
    private int productId;

    public PurchaseDTO(int buyerId, int productId) {
        this.buyerId = buyerId;
        this.productId = productId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "Purchase{buyerId=" + buyerId + ", productId=" + productId + "}";
    }
}
