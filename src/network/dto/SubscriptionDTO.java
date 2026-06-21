package network.dto;

import java.io.Serializable;

public class SubscriptionDTO implements Serializable {
    private int id;
    private int subscriberId;
    private String subscriberUsername;
    private int artistId;
    private String artistUsername;
    private boolean isActive;

    public SubscriptionDTO(int id, int subscriberId, String subscriberUsername,
                           int artistId, String artistUsername, boolean isActive) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.subscriberUsername = subscriberUsername;
        this.artistId = artistId;
        this.artistUsername = artistUsername;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public String getSubscriberUsername() {
        return subscriberUsername;
    }

    public int getArtistId() {
        return artistId;
    }

    public String getArtistUsername() {
        return artistUsername;
    }

    public boolean isActive() {
        return isActive;
    }
}