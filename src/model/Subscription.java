package model;

import java.time.LocalDate;
import java.util.Objects;

public class Subscription {
    private int id;
    private Subscriber subscriber;
    private Artist artist;
    private LocalDate startdate;
    private boolean isActive;

    public Subscription(int id, Subscriber subscriber, Artist artist) {
        this.id = id;
        this.subscriber = subscriber;
        this.artist = artist;
        this.startdate = LocalDate.now();
        this.isActive = true;
    }

    public Subscription(int id, Subscriber subscriber, Artist artist, LocalDate startdate, boolean isActive) {
        this.id = id;
        this.subscriber = subscriber;
        this.artist = artist;
        this.startdate = startdate;
        this.isActive = isActive;
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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        String subscriberName = (subscriber != null) ? subscriber.getUsername() : "null";
        String artistName = (artist != null) ? artist.getUsername() : "null";

        return "Subscription {" +
                "Id=" + id +
                ", Subscriber=" + subscriberName +
                ", Artist=" + artistName +
                ", Start Date=" + startdate +
                ", IsActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Subscription subscription)) return false;
        return id == subscription.id &&
                isActive == subscription.isActive &&
                Objects.equals(subscriber, subscription.subscriber) &&
                Objects.equals(artist, subscription.artist) &&
                Objects.equals(startdate, subscription.startdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subscriber, artist, startdate, isActive);
    }
}