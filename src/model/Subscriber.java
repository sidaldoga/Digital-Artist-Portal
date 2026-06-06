package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Subscriber extends User {
    private Address address;
    private List<Artist> subscriptionlist;
    private int subscriberpoint = 0;

    public Subscriber(String fname, String lname, String username, String password, Address address, List<Artist> subscriptionlist, Integer subscriberpoint, String email) {
        super(fname, lname, username, password, email);
        this.address = address;

        this.subscriptionlist = (subscriptionlist != null) ? subscriptionlist : new ArrayList<>();
        this.subscriberpoint = (subscriberpoint != null) ? subscriberpoint : 0;
    }

    public Subscriber(int id, String fname, String lname, String username, String password, Address address, List<Artist> subscriptionlist, Integer subscriberpoint, String email) {
        super(id, fname, lname, username, password, email);
        this.address = address;
        this.subscriptionlist = (subscriptionlist != null) ? subscriptionlist : new ArrayList<>();
        this.subscriberpoint = (subscriberpoint != null) ? subscriberpoint : 0;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Artist> getSubscriptionlist() {
        return subscriptionlist;
    }

    public void setSubscriptionlist(List<Artist> subscriptionlist) {
        this.subscriptionlist = subscriptionlist;
    }

    public int getSubscriberpoint() {
        return subscriberpoint;
    }

    public void setSubscriberpoint(int subscriberpoint) {
        this.subscriberpoint = subscriberpoint;
    }

    @Override
    public String toString() {
        int subCount = (subscriptionlist != null) ? subscriptionlist.size() : 0;

        return "Subscriber { " +
                "ID=" + getId() +
                ", Username=" + getUsername() +
                ", Address=" + address +
                ", Total Subscriptions=" + subCount +
                ", Subscriber Point=" + subscriberpoint +
                ", Email=" + getEmail() +
                " }";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Subscriber subscriber)) return false;
        if (!super.equals(object)) return false;

        return subscriberpoint == subscriber.subscriberpoint &&
                Objects.equals(address, subscriber.address) &&
                Objects.equals(subscriptionlist, subscriber.subscriptionlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, subscriptionlist, subscriberpoint);
    }
}
    