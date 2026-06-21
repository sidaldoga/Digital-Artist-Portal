package model;

import java.util.Objects;

public class Purchase {
    private int id;
    private int buyerId;
    private int productId;
    private String purchaseDate;

    public Purchase(int id, int buyerId, int productId, String purchaseDate) {
        this.id = id;
        this.buyerId = buyerId;
        this.productId = productId;
        this.purchaseDate = purchaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "Purchase {" +
                "Id=" + id +
                ", BuyerId=" + buyerId +
                ", ProductId=" + productId +
                ", PurchaseDate='" + purchaseDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Purchase purchase)) return false;
        return id == purchase.id &&
                buyerId == purchase.buyerId &&
                productId == purchase.productId &&
                Objects.equals(purchaseDate, purchase.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, buyerId, productId, purchaseDate);
    }
}