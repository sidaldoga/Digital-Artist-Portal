package model;

import java.time.LocalDate;
import java.util.Objects;

public class Subscription {
    private int id;
    private Subscriber subscriber;
    private ArtProduct product;
    private LocalDate startdate;
    private boolean isActive;

    public Subscription(int id, Subscriber subscriber, ArtProduct product) {
        this.id = id;
        this.subscriber = subscriber;
        this.product = product;
        this.startdate = LocalDate.now();
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public ArtProduct getProduct() {
        return product;
    }

    public void setProduct(ArtProduct product) {
        this.product = product;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString(){
        return "Subscription {"+"Id="+id+", Subscriber="+subscriber+"\n" + "Product="+product+"\n" + "Start Date="+startdate+"\n" + "IsActive="+isActive+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof Subscription subscription))return false;
        return Objects.equals(id,subscription.id) && Objects.equals(subscriber, subscription.subscriber) && Objects.equals(product,subscription.product)&&Objects.equals(startdate,subscription.startdate) &&Objects.equals(isActive,subscription.isActive);

    }

    @Override
    public int hashCode(){
        return Objects.hash(id,subscriber,product,startdate,isActive);
    }

}
